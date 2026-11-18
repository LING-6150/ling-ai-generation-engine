# Two-user pilot

The `pilot` Spring profile enables direct HTML and multi-file generation. It rejects Vue builds and agent workflows. The frontend reads the available mode from the app response. Each private preview URL grants five minutes of read access to one project directory. Refreshing the preview as the owner or an administrator issues a new URL. Publishing creates a separate public copy.

## Local setup

Use disposable MySQL and Redis instances and synthetic accounts. Apply `sql/create_table.sql` to MySQL. Build the frontend with `VITE_API_BASE_URL=/api` and `npm run build`. Package the Java 21 backend with `./mvnw package`.

Load the database and model settings from `application-local.example.yml` or a deployment-only configuration. Start the JAR with `--spring.profiles.active=local,pilot --server.address=127.0.0.1 --server.port=38463`. Set these environment variables for the loopback run.

| Setting | Local value |
|---|---|
| `PILOT_FRONTEND_ORIGIN` | `http://127.0.0.1:38464` |
| `PILOT_PREVIEW_BASE_URL` | `http://localhost:38465/api/preview` |
| `PILOT_PUBLISH_BASE_URL` | `http://localhost:38465/published` |

Serve the built frontend and proxy its `/api/` requests to the backend. Keep the session cookie host-only. The artifact host serves only ticketed previews and published files. The example below uses Nginx inside its `http` block. Substitute the actual frontend output directory and the backend working directory.

```nginx
server {
    listen 127.0.0.1:38464;
    server_name 127.0.0.1;
    root /absolute/path/to/Ling-AI-CODE-generation-frontend/dist;
    location / { try_files $uri $uri/ /index.html; }
    location /api/preview/ { return 404; }
    location /api/ {
        proxy_pass http://127.0.0.1:38463;
        proxy_buffering off;
        proxy_read_timeout 300s;
        access_log off;
    }
}

server {
    listen 127.0.0.1:38465;
    server_name localhost;
    location /api/preview/ {
        proxy_pass http://127.0.0.1:38463;
        proxy_set_header Cookie "";
        proxy_hide_header Set-Cookie;
        access_log off;
    }
    location /published/ {
        alias /absolute/path/to/backend-working-directory/tmp/code_deploy/;
        index index.html;
        autoindex off;
    }
    location / { return 404; }
}
```

## Repeat the acceptance run

Use dummy values for `DEEPSEEK_API_KEY` and `PEXELS_API_KEY` in the disposable backend. Add the following arguments to the JAR command. The test selects both generation modes explicitly, starts the streaming model fixture on port 38466 and closes it on exit.

```text
--langchain4j.open-ai.chat-model.base-url=http://127.0.0.1:38466/v1
--langchain4j.open-ai.streaming-chat-model.base-url=http://127.0.0.1:38466/v1
--langchain4j.open-ai.reasoning-streaming-chat-model.base-url=http://127.0.0.1:38466/v1
--langchain4j.open-ai.routing-chat-model.base-url=http://127.0.0.1:38466/v1
--langchain4j.structured-routing-chat-model.base-url=http://127.0.0.1:38466/v1
--langchain4j.review-model.zhipu.base-url=http://127.0.0.1:38466/v1
```

The fixture smoke script is provisioned locally in the ignored `tmp/` directory. Install Playwright and its Chromium browser to run it.

```sh
python -m pip install playwright
python -m playwright install chromium
python tmp/pilot_smoke.py
```

The run creates two synthetic accounts, two drafts and one published HTML release in the disposable stack. It checks browser login, cross-account access, generation, ZIP contents, CSS and JavaScript execution, preview origin and cookie scope. A deliberate provider failure must produce an error and leave the public release byte-for-byte intact. The script leaves the records and artifacts available for inspection.

The local fixture measures application behavior. Live-model quality, worker isolation for Vue, resilience during redeployment, and concurrent production load require separate trials.
