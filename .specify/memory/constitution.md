# English Prep AI Constitution

**Version**: 1.0.0 | **Ratified**: 2026-11-16 | **Last Amended**: 2026-11-16

## Core Principles

### I. Offline-First & Local Privacy (NON-NEGOTIABLE)
All core learning, tryout packs, listening playback, deterministic scoring, progress analytics, local Gemma 4 chat/RAG, and local Whisper ONNX STT MUST execute completely offline once assets are bundled or imported via Android Storage Access Framework (SAF). `INTERNET` permission is NOT required for core runtime flows. User answers, recordings, and imported models remain strictly on device with zero hidden telemetry or remote API calls.

### II. Exam Integrity & Non-Official Provenance
TOEFL ITP Level 1 (310–677), TOEFL iBT 2026 (post-2026-01-21 format, 1–6 scale), and IELTS Academic (0–9 bands) MUST stay explicitly differentiated in UI, blueprints, question types, and scoring rules. Speaking/Writing are excluded from standard TOEFL ITP Level 1 core tests. Objective grading is strictly deterministic; AI coaching and score estimates are visibly labeled non-official estimates. Copyrighted ETS/British Council/Cambridge banks MUST NEVER be copied; incomplete packs MUST display `Sample` status and exact item counts (`available/blueprint`).

### III. Clean MVVM, UDF & Pragmatic Simplicity
Single `:app` Gradle module with unidirectional dependency flow (`ui -> domain <- data`) and UDF (`ViewModel -> StateFlow<UiState> -> Stateless Screen -> Components`). Apply SOLID, DRY, KISS, YAGNI, and minimal-diff discipline. No Firebase, remote auth, analytics SDK, or speculative multi-module boilerplate.

### IV. Spec-Driven & Test-First Discipline (NON-NEGOTIABLE)
Features follow Spec Kit (`constitution -> specify -> plan -> tasks -> implement -> converge`) with BMAD review gates in `docs/reviews/`. Pure domain scoring, normalization, pack validation, SAF path safety, and ViewModels are built TDD-first (failing test -> minimal implementation -> green verification) before UI wiring.

### V. Human-Centered Accessible Compose UI (Anti-AI-Slop)
Material 3 editorial restraint with one accent (`deep teal`), 4dp/8dp spacing rhythm, zero gradient/chip clutter, zero floating chat overlays over exam screens, <=2–3 tap depth from Home, >=48dp touch targets, >=8dp target spacing, WCAG 2.2 AA contrast, TalkBack semantics, 200% font scaling without clipping, reduced-motion support, and edge-to-edge + IME inset handling.

## Runtime & Security Constraints
- `minSdk = 26`, `targetSdk = 35`, Kotlin + Jetpack Compose + Material 3 + Hilt + Room (with schema export & migrations) + DataStore + Media3 ExoPlayer + LiteRT-LM (`0.13.1`) + ONNX Runtime (`1.22.0`).
- SAF document/tree URIs MUST persist read permissions safely, validate filename/size/magic/SHA-256/disk/ABI/RAM, reject zip traversal (`..`) and decompression bombs, copy atomically into app-private storage (`files/models/`, `files/packs/`), and recover gracefully on revoked permissions or corrupt files.
- Gemma 4 compatibility is enabled ONLY for verified `.litertlm` runtimes via `LocalLlmEngine`; Whisper ONNX runs via `SpeechToTextEngine` with graceful fallback when models are absent or incompatible.

## Governance
- `AGENTS.md`, `PRD.md`, `DESIGN.md`, `ARSITEKTUR.md`, and `DATA_SCHEMA.md` are authoritative alongside this Constitution.
- No feature is declared complete without verified unit/UI test evidence and physical/emulator smoke verification.
