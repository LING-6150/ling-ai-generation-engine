# Local code and image generation

The `qwen` profile routes code generation, routing and review to a chat-completions server. The `krea` profile enables the project image button and the authenticated image API. Either profile can be added after the engine's normal database and session profile. For the static pilot, use `local,qwen,krea,pilot`. Configure MySQL, Redis and the preview origin as described in the [pilot setup](two-user-pilot.md).

## Connect the model servers

Start a loopback llama.cpp server for text generation and a Krea-capable stable-diffusion.cpp server for images. The Windows pilot used llama.cpp b12497 with Qwen3-VL-4B Instruct on CPU, and stable-diffusion.cpp 290148d with the Krea 2 Turbo Q4 diffusion model, Qwen text encoder and Wan VAE on CUDA. Deployment launchers and host paths are maintained locally under `deployment/`.

Set these variables in the shell that starts the engine. The model names must match the respective servers' `GET /v1/models` responses.

```powershell
$env:QWEN_BASE_URL = 'http://127.0.0.1:8000/v1'
$env:QWEN_MODEL = 'Qwen3-local'
$env:KREA_BASE_URL = 'http://127.0.0.1:30000/v1'
$env:KREA_MODEL = 'sd-cpp-local'
$env:KREA_IMAGE_SIZE = '512x512'
$env:SPRING_PROFILES_ACTIVE = 'local,qwen,krea,pilot'
java -jar target/Ling-AI-CODE-generation-0.0.1-SNAPSHOT.jar
```

Existing inference servers can also be used. Qwen must support OpenAI-compatible chat completions and streaming. Krea must accept `POST /v1/images/generations` and return `data[0].b64_json`. For SGLang the profile defaults to `krea/Krea-2-Turbo` and `1024x1024`. Set `QWEN_API_KEY` or `KREA_API_KEY` if your inference server requires a bearer token. [Qwen deployment](https://github.com/QwenLM/Qwen3) · [SGLang Krea 2 deployment](https://docs.sglang.io/cookbook/diffusion/Krea/Krea-2)

## Run the MCP smoke test

Build the companion MCP checkout with Java 21 and `.\mvnw.cmd package`. Sign into the engine as a pilot user and set `CODEGEN_SESSION_COOKIE` in the test process to that user's current cookie header. Set `CODEGEN_BACKEND_URL` to the engine origin, including the port. The default is `http://127.0.0.1:8123`. The test command below uses the locally provisioned smoke script in the ignored `tmp/` directory. On a fresh checkout, start the MCP jar directly and call the three tools from an SSE client.

```powershell
python tmp/local_mcp_smoke.py --mcp-jar ..\ling-codegen-mcp-server\target\ling-codegen-mcp-server-0.0.1-SNAPSHOT.jar
```

The smoke starts its own loopback MCP server, negotiates an SSE session and discovers all three tools. It creates a real project, generates an HTML page with a Krea image, adds a second image and verifies the HTML image reference and both PNGs in the owner's downloaded ZIP. It prints the project ID and authenticated preview URL, then stops its MCP server. The project remains available to its owner. To run MCP continuously, launch the same jar directly and connect the client to `http://127.0.0.1:8127/sse`. Set `CODEGEN_FRONTEND_URL` when the editor runs on a different origin from the default `http://localhost:5173`.
