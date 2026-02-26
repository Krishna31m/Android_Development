# 🤖 AI Career Mentor
### Production-ready Android App — University Development Challenge

![Android](https://img.shields.io/badge/Android-API%2026+-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blueviolet)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue)
![AI](https://img.shields.io/badge/AI-Gemini%201.5%20Flash-red)

---

## ⚡ Quick Setup (3 steps)

1. **Get a free Gemini API key** → [aistudio.google.com](https://aistudio.google.com/app/apikey)
2. **Create `local.properties`** in project root:
   ```
   GEMINI_API_KEY=your_key_here
   sdk.dir=/path/to/your/Android/sdk
   ```
3. **Open in Android Studio Ladybug → Sync Gradle → Run**

---

## ✨ Features

| Feature | What it does |
|---|---|
| 📄 **Resume Analyzer** | Upload PDF → AI extracts text, gives Overall Score, ATS Score, skill gaps, strengths, and improvement tips |
| 🔍 **Skill Gap Detector** | Pick a target role → AI compares skills, shows % match, builds a phase-by-phase learning roadmap |
| 🎤 **Mock Interview** | 10 AI-generated questions for your role → type answers → receive score (0-10) + detailed feedback |
| 🗺️ **Career Roadmap** | Full Beginner→Expert plan with resources, projects, milestones, salary range, and hiring companies |
| 📋 **History** | All analyses saved in Room Database with timestamps and scores |

---

## 🏗️ Architecture

```
presentation/       ← Jetpack Compose screens + ViewModels (MVVM)
  ├── navigation/
  ├── theme/         ← Material 3 + dark mode
  ├── components/    ← Shared reusable composables
  └── screens/
      ├── home/
      ├── resume/
      ├── skillgap/
      ├── interview/
      ├── roadmap/
      └── history/

domain/             ← Business logic (pure Kotlin)
  ├── model/        ← Data models
  ├── repository/   ← Interfaces
  └── usecase/      ← Use cases (single responsibility)

data/               ← Data sources
  ├── remote/       ← Gemini API + prompt templates
  ├── local/        ← Room DB (entities, DAOs)
  └── repository/   ← Repository implementations

core/               ← Cross-cutting
  ├── di/           ← Hilt modules
  ├── database/     ← AppDatabase
  └── utils/        ← PDF extractor
```

**Patterns used:** MVVM · Clean Architecture · Repository · Sealed UiState · Hilt DI · Coroutines + StateFlow

---

## 🛠️ Tech Stack

| | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Database | Room |
| Async | Coroutines + StateFlow |
| PDF | PDFBox Android |
| Shimmer | compose-shimmer |
| AI | Google Gemini 1.5 Flash |

---

## 🔒 Security
- API key in `local.properties` (git-ignored) → injected via `BuildConfig` at compile time
- Never hardcoded in source code
- Proguard configured for release builds

---

## 🎯 Demo Script
1. **Home** — Show gradient dashboard with animated card entrance
2. **Resume** — Upload a PDF → tap Analyze → show scores + improvement suggestions
3. **Skill Gap** — Pick "Android Developer" → show match % + roadmap phases
4. **Interview** — Select role → answer 2-3 questions → show AI scoring
5. **Roadmap** — Generate for "Data Scientist" → show 4-phase plan
6. **History** — Show all saved analyses with timestamps
