"""Run the two-account acceptance test against a disposable loopback pilot."""
import argparse
import io
import json
import threading
import uuid
import zipfile
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit

from playwright.sync_api import sync_playwright


TITLE = "Pilot status board"
HTML = f"<!doctype html><html><body><h1>{TITLE}</h1></body></html>"
MULTI = HTML.replace("<h1>", '<link rel="stylesheet" href="style.css"><h1>').replace(
    "</body>", '<script src="script.js"></script></body>'
)


class ModelFixture(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/v1/chat/completions":
            self.send_error(404)
            return
        payload = json.loads(self.rfile.read(int(self.headers["Content-Length"])))
        prompt = payload["messages"][-1]["content"]
        self.server.prompts.append(prompt)
        if "simulate provider outage" in prompt:
            self.reply(400, "application/json", '{"error":{"message":"pilot fixture failure"}}')
            return
        result = f"```html\n{HTML}\n```"
        if "separate assets" in prompt:
            result = (f"```html\n{MULTI}\n```\n```css\nh1 {{ color: rgb(20, 40, 60) }}\n```"
                      "\n```js\ndocument.querySelector('h1').dataset.ready = 'yes'\n```")
        events = []
        for delta, stop in [({"role": "assistant", "content": result}, None), ({}, "stop")]:
            chunk = {"id": "pilot", "object": "chat.completion.chunk", "created": 1, "model": "fixture",
                     "choices": [{"index": 0, "delta": delta, "finish_reason": stop}]}
            events.append("data: " + json.dumps(chunk) + "\n\n")
        self.reply(200, "text/event-stream", "".join(events) + "data: [DONE]\n\n")

    def reply(self, status, content_type, text):
        body = text.encode()
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


def success(response):
    assert response.ok, response.status
    payload = response.json()
    assert payload["code"] == 0, payload.get("message")
    return payload.get("data")


def register(client, api):
    credentials = {"userAccount": "pilot" + uuid.uuid4().hex[:12], "userPassword": uuid.uuid4().hex}
    success(client.post(api + "/user/register", data={**credentials, "checkPassword": credentials["userPassword"]}))
    return credentials


def local_page(browser):
    page = browser.new_page()
    def route_local(route):
        if urlsplit(route.request.url).hostname in {"localhost", "127.0.0.1", "::1"}:
            route.continue_()
        else:
            route.abort()
    page.route("**/*", route_local)
    return page


def run(browser, frontend, preview, publish, prompts):
    api = frontend + "/api"
    alice_page = local_page(browser)
    alice = alice_page.request
    success(alice.get(api + "/health/"))
    credentials = register(alice, api)
    alice_page.goto(frontend + "/user/login")
    alice_page.get_by_placeholder("Account", exact=True).fill(credentials["userAccount"])
    alice_page.get_by_placeholder("Password", exact=True).fill(credentials["userPassword"])
    alice_page.locator("form").get_by_role("button", name="Sign In").click()
    alice_page.wait_for_url(frontend + "/")

    draft = success(alice.post(api + "/app/add", data={"initPrompt": TITLE, "codeGenType": "html"}))
    stream = api + "/app/chat/gen/code"
    events = alice.get(stream, params={"appId": draft, "message": TITLE}).text()
    assert "event:done" in events and TITLE in events
    with zipfile.ZipFile(io.BytesIO(alice.get(api + f"/app/download/{draft}").body())) as archive:
        assert archive.read("index.html").decode().strip() == HTML
    release = success(alice.post(api + "/app/deploy", data={"appId": draft}))
    assert release.startswith(publish + "/")
    anonymous = browser.new_context().request
    original = anonymous.get(release)
    assert original.ok and original.text().strip() == HTML
    assert anonymous.get(api + "/app/get/vo", params={"id": draft}).json()["code"] == 40100

    bob_page = local_page(browser)
    bob = bob_page.request
    success(bob.post(api + "/user/login", data=register(bob, api)))
    assert bob.get(api + "/app/get/vo", params={"id": draft}).json()["code"] == 40101
    assert bob.get(api + f"/static/html_{draft}/index.html", max_redirects=0).status == 403
    assert bob.get(api + f"/chatHistory/app/{draft}").json()["code"] == 40101
    assert bob.get(api + f"/app/download/{draft}").json()["code"] == 40101
    assert bob.post(api + "/app/deploy", data={"appId": draft}).json()["code"] == 40101
    print("PASS: login, HTML generation, ZIP, public release and private draft access", flush=True)

    rejected = bob.post(api + "/app/add", data={"initPrompt": TITLE, "codeGenType": "vue_project"})
    assert rejected.json()["code"] == 40000
    other = success(bob.post(api + "/app/add", data={"initPrompt": TITLE, "codeGenType": "multi_file"}))
    assert alice.get(api + "/app/get/vo", params={"id": other}).json()["code"] == 40101
    blocked = bob.get(stream, params={"appId": other, "message": TITLE, "agent": "true"}).text()
    assert "business-error" in blocked and len(prompts) == 1
    events = bob.get(stream, params={"appId": other, "message": "Use separate assets"}).text()
    assert "event:done" in events and len(prompts) == 2
    bob_page.goto(frontend + f"/app/{other}")
    heading = bob_page.frame_locator("iframe").locator('h1[data-ready="yes"]')
    heading.wait_for()
    assert heading.evaluate("el => getComputedStyle(el).color") == "rgb(20, 40, 60)"
    frame = next(frame for frame in bob_page.frames if frame.url.startswith(preview + "/"))
    assert frame.evaluate("self.origin") == "null"
    session_names = {c["name"] for c in bob_page.context.cookies(api + "/user/get/login")}
    assert session_names and not any(c["name"] in session_names for c in bob_page.context.cookies(frame.url))
    print("PASS: pilot mode restrictions, JavaScript, CSS and preview cookie isolation", flush=True)

    failed = alice.get(stream, params={"appId": draft, "message": "simulate provider outage"}).text()
    assert "business-error" in failed or "workflow_error" in failed
    after = anonymous.get(release)
    assert len(prompts) >= 3 and after.ok and after.body() == original.body()
    print("PASS: failed generation preserved the published release", flush=True)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--frontend", default="http://127.0.0.1:38464")
    parser.add_argument("--preview", default="http://localhost:38465/api/preview")
    parser.add_argument("--publish", default="http://localhost:38465/published")
    parser.add_argument("--model-port", type=int, default=38466)
    args = parser.parse_args()
    for url in (args.frontend, args.preview, args.publish):
        if urlsplit(url).hostname not in {"localhost", "127.0.0.1", "::1"}:
            parser.error("Use the local disposable pilot for this fixture run.")
    with ThreadingHTTPServer(("127.0.0.1", args.model_port), ModelFixture) as model:
        model.prompts = []
        threading.Thread(target=model.serve_forever, daemon=True).start()
        try:
            with sync_playwright() as playwright:
                browser = playwright.chromium.launch()
                run(browser, args.frontend.rstrip("/"), args.preview.rstrip("/"), args.publish.rstrip("/"), model.prompts)
                browser.close()
        finally:
            model.shutdown()
