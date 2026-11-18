"""Serve the local GGUF bundle with llama.cpp or stable-diffusion.cpp."""
import argparse
import os
from pathlib import Path
import subprocess


def existing(path):
    if not path.is_file():
        raise SystemExit(f"File missing: {path}")
    return str(path.resolve())


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("provider", choices=("qwen", "krea"))
    parser.add_argument("--weights", required=True, type=Path)
    parser.add_argument("--server", required=True, type=Path)
    parser.add_argument("--qwen-model", type=Path, help="Override the bundle's Qwen3-VL Instruct GGUF for code")
    parser.add_argument("--cuda-dll-directory", type=Path)
    parser.add_argument("--krea-vram-gib", default="5.5")
    parser.add_argument("--port", type=int)
    args = parser.parse_args()
    server = existing(args.server)
    encoder = args.weights / "text-encoder/Qwen3VL-4B-Instruct-Q4_K_M.gguf"
    env = os.environ.copy()
    if args.provider == "qwen":
        options = ["--host", "127.0.0.1", "--port", str(args.port or 8000), "--alias", "Qwen3-local",
                   "-m", existing(args.qwen_model or encoder), "-ngl", "0", "-c", "8192", "-np", "1", "--reasoning", "off"]
    else:
        if args.cuda_dll_directory:
            env["PATH"] = str(args.cuda_dll_directory.resolve(strict=True)) + os.pathsep + env["PATH"]
        options = ["--listen-ip", "127.0.0.1", "--listen-port", str(args.port or 30000),
                   "--diffusion-model", existing(args.weights / "krea2-turbo/krea2_turbo-Q4_K_M.gguf"),
                   "--llm", existing(encoder), "--vae", existing(args.weights / "vae/split_files/vae/wan_2.1_vae.safetensors"),
                   "--backend", "te=cuda0,vae=cuda0,diffusion=cuda0", "--offload-to-cpu", "--max-vram", args.krea_vram_gib,
                   "--flash-attn", "on", "--steps", "8", "--cfg-scale", "1", "--rng", "cpu", "-W", "512", "-H", "512"]
    return subprocess.run([server, *options], env=env).returncode


if __name__ == "__main__":
    raise SystemExit(main())
