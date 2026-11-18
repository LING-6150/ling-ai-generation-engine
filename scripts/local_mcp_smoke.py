"""Start a local MCP jar and exercise both model providers against a running engine."""
import argparse
import io
import json
import os
import socket
import subprocess
import time
import zipfile
from contextlib import contextmanager
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import urljoin, urlsplit
from urllib.request import Request, urlopen


class ImageSources(HTMLParser):
    def __init__(self):
        super().__init__()
        self.paths = []

    def handle_starttag(self, tag, attrs):
        if tag == "img":
            self.paths.append(dict(attrs).get("src"))


@contextmanager
def start_mcp(jar, port, java):
    with socket.socket() as probe:
        probe.bind(("127.0.0.1", port))
        port = probe.getsockname()[1]
    host = f"http://127.0.0.1:{port}"
    proc = subprocess.Popen([java, "-jar", str(jar), "--server.address=127.0.0.1", f"--server.port={port}"])
    try:
        for _ in range(240):
            if proc.poll() is not None:
                raise RuntimeError(f"MCP server exited with status {proc.returncode}")
            try:
                with socket.create_connection(("127.0.0.1", port), timeout=.5):
                    break
            except OSError:
                time.sleep(.25)
        else:
            raise TimeoutError("MCP server did not start in 60 seconds")
        yield host
    finally:
        if proc.poll() is None:
            proc.terminate()
            try:
                proc.wait(timeout=15)
            except subprocess.TimeoutExpired:
                proc.kill()
                proc.wait()


def read_event(stream):
    data = []
    for line in stream:
        line = line.decode().rstrip("\r\n")
        if line.startswith("data:"):
            data.append(line[5:].lstrip())
        if not line and data:
            return "\n".join(data)
    raise EOFError("MCP event stream closed")


def exercise(host, engine, cookie):
    with urlopen(Request(host + "/sse", headers={"Accept": "text/event-stream"}), timeout=600) as stream:
        endpoint = urljoin(host, read_event(stream))
        if urlsplit(endpoint).netloc != urlsplit(host).netloc:
            raise ValueError("Unexpected MCP message endpoint")

        def rpc(method, params, call_id=None):
            body = {"jsonrpc": "2.0", "method": method, "params": params}
            if call_id is not None:
                body["id"] = call_id
            request = Request(endpoint, json.dumps(body).encode(), {"Content-Type": "application/json"})
            with urlopen(request, timeout=600):
                pass
            if call_id is not None:
                while True:
                    reply = json.loads(read_event(stream))
                    if reply.get("id") == call_id:
                        if "error" in reply:
                            raise RuntimeError(reply["error"])
                        return reply["result"]

        def call(name, arguments, call_id):
            reply = rpc("tools/call", {"name": name, "arguments": arguments}, call_id)
            content = reply["content"][0]["text"]
            if reply.get("isError"):
                raise RuntimeError(content)
            return json.loads(content)

        rpc("initialize", {"protocolVersion": "2024-11-05", "capabilities": {},
                           "clientInfo": {"name": "local-generation-smoke", "version": "1"}}, 1)
        rpc("notifications/initialized", {})
        names = {tool["name"] for tool in rpc("tools/list", {}, 2)["tools"]}
        if not {"generateWebsite", "generateImage", "listRecentApps"} <= names:
            raise RuntimeError(f"Missing MCP tools in {names}")
        draft = call("generateWebsite", {"prompt": "Create a brief HTML page titled Local Garden with a heading and the supplied image.",
                                        "codeGenType": "html", "imagePrompt": "A single green leaf"}, 3)
        app_id = draft["appId"]
        second = call("generateImage", {"appId": app_id, "prompt": "A small tree"}, 4)
        recent = call("listRecentApps", {}, 5)
        if not any(str(app["id"]) == app_id for app in recent):
            raise RuntimeError(f"App {app_id} missing from recent projects")

    request = Request(engine + "/api/app/download/" + app_id, headers={"Cookie": cookie})
    with urlopen(request, timeout=30) as resp, zipfile.ZipFile(io.BytesIO(resp.read())) as archive:
        sources = ImageSources()
        sources.feed(archive.read("index.html").decode())
        image = draft["imagePath"]
        if image not in sources.paths or image == second["path"]:
            raise RuntimeError("Generated HTML did not reference the requested project image or its asset was overwritten")
        for path in (image, second["path"]):
            if not archive.read(path).startswith(b"\x89PNG\r\n\x1a\n"):
                raise RuntimeError(f"Invalid generated PNG at {path}")
    print(f"PASS app {app_id}: local MCP generated HTML and two PNGs. Preview {draft['previewUrl']}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mcp-jar", required=True, type=Path)
    parser.add_argument("--java", default="java")
    parser.add_argument("--port", default=0, type=int, help="MCP loopback port (default: assigned by OS)")
    args = parser.parse_args()
    session = os.environ.get("CODEGEN_SESSION_COOKIE")
    if not session:
        parser.error("Set CODEGEN_SESSION_COOKIE to the pilot user's current engine session cookie")
    backend = os.environ.get("CODEGEN_BACKEND_URL", "http://127.0.0.1:8123").rstrip("/")
    with start_mcp(args.mcp_jar, args.port, args.java) as host:
        exercise(host, backend, session)
