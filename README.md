<p align="center">
  <img src="docs/images/logo.png" alt="JobRadar" width="72"/>
</p>

<h1 align="center">JobRadar</h1>

<p align="center">
  <b>Manage your job hunting lifecycle, not just job search.</b><br/>
  An AI-native workspace for your entire job hunting journey - from discovering opportunities to tracking interviews and offers.
</p>

<p align="center">
  <a href="#quick-start"><b>Quick Start</b></a> · <a href="ROADMAP.md">Roadmap</a>
</p>

<p align="center">
  <img src="docs/images/hero.png" alt="JobRadar" width="680"/>
</p>

---

## Features

- 📱 **Native Android App** - Kotlin · Jetpack Compose · MVI
- 🌐 **Web Dashboard** - Next.js · real-time sync across devices
- 🤖 **AI Insights** - job analysis · match score · company profile
- 📊 **Job Pipeline** - favorite → apply → interview → offer
- 📄 **Resume Workspace** - your resume, part of the product
- 🔌 **Pluggable Providers** - job data sources are swappable plugins

---

## Quick Start

```bash
# Backend (zero-config · Mock data)
cd backend && ./gradlew bootRun
# Web
cd web && pnpm install && pnpm dev
# Android
cd android && ./gradlew :app:assembleDebug
```

---

## Architecture

```mermaid
graph TD
  Android[📱 Android Native] --> API[(Backend · Spring Boot)]
  Web[🌐 Web Dashboard] --> API
  API --> P[🔌 Pluggable Provider]
  P --> Mock[Mock Data]
  P --> CSV[CSV]
  API --> AI[🤖 AI Layer]
```

One shared contract `{ code, message, data }` · real-time sync · data sources fully pluggable.

---

## Roadmap

**v0.1 Foundation → v0.2 Connect → v0.3 Insight → v0.4 Automation → v1.0 Agent**

- ✅ **v0.1 · Foundation** - end-to-end skeleton · pluggable data source
- ✅ **v0.2 · Connect** - Web / App / Backend data pipeline linked
- 🔜 **v0.3 · Insight** - AI makes jobs understandable & comparable
- ⏳ **v0.4 · Automation** - subscription / reminder / interview log
- ⏳ **v1.0 · Agent** - auto search / recommend / authorized apply

---

## Why JobRadar

Job hunting is not just "searching for a job" - it is **managing the whole journey from apply to offer**. JobRadar helps you manage your career like a product, with AI applied to real decisions.

---

## License

MIT

---

## Contributing

- [Contributing guide](CONTRIBUTING.md) · [Code of Conduct](CODE_OF_CONDUCT.md)

---

<p align="center">
  <b>A real product starts with its first users.</b><br/>
  If you find it useful, give a ⭐; if you find a bug, open an <a href="https://github.com/yanghaozhe883/job-radar/issues">Issue</a>.
</p>
