# Zerntra – Self-Hosted Discord Music Bot

> **Tired of YouTube-Blocked Music Bots?**  
> **Bypass the restrictions by hosting your own Zerntra instance.**

Zerntra is a **self-hostable, modular, and privacy-friendly Discord music bot** built for flexibility and privacy.  
It uses **YouTube music sources by default**, but you can use many other sources aswell and enable Spotify using your own API key.

---

## ✨ Features

- 🎵 Play music from many sources like YouTube, Spotify or Soundcloud
- 💬 Easy-to-use Slash Commands (`/play`, `/skip`, `/stop`, ...)
- 🧩 Modular codebase – easy to extend and maintain
- 🛡️ Privacy-focused: no built-in tracking or bundled API keys
- 🗑️ Clean-chat policy: Automatically deletes messages that are no longer needed

---

## 🚀 Quick Start
coming soon...

---

## Development guide
### Build docker image
#### For local operating system:
> **docker build -t zentra-bot .**
#### For arm64 based operating system (e.g. Raspberry Pi):
> **docker buildx build --platform linux/arm64 -t zerntra-bot --load .**

### Create .tar file from image for exports
> **docker save -o zerntra-bot.tar zerntra-bot**

### Load .tar file to docker
> **docker load -i zerntra-bot.tar**

### Run docker container
> **Add your secrets to the .env file**

> **docker run -d --name zerntra-bot --env-file .env --add-host=host.docker.internal:host-gateway --restart unless-stopped zerntra-bot:latest**