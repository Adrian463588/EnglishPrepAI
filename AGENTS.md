# AGENTS.md — English Prep AI
Version: 2026-11-16. Scope: Codex, Claude Code, Google Antigravity / Gemini, and similar coding agents.

## Mission
Implement an offline-first Android native study app for TOEFL ITP Level 1, TOEFL iBT 2026, and IELTS Academic. Source of truth: PRD.md, DESIGN.md, ARSITEKTUR.md, DATA_SCHEMA.md, content/CURRICULUM.md, packs/*.json. Never declare features implemented just because documented.

## Non-negotiables
1. Kotlin + Jetpack Compose + Material 3, ViewModel, Flow/StateFlow, immutable UI state, UDF, lifecycle-safe collectors.
2. Clean boundaries: UI -> ViewModel -> UseCase (only for substantial reusable business logic) -> Repository -> Local sources/engines. No Android APIs in pure scoring logic.
3. Apply KISS, YAGNI, DRY, SOLID pragmatically. No unnecessary multi-module architecture, backend, Firebase, authentication, analytics SDK, or generic frameworks.
4. Offline-first: INTERNET permission is not required for runtime core. A user-initiated online download is optional and must not be disguised as offline; prefer external browser/file manager and SAF import for MVP.
5. User data and models stay on device. No hidden telemetry, third-party API calls, or raw audio uploads.
6. Distinguish TOEFL ITP vs TOEFL iBT 2026 vs IELTS Academic explicitly. Speaking/Writing are not part of standard ITP Level 1 core exam; optional supplementary practice may be offered separately.
7. Objective answers are scored deterministically, never by LLM. AI explanations must quote local source IDs when available and display uncertainty. AI subjective writing/speaking feedback = non-official estimated feedback.
8. Never scrape, redistribute, or reproduce protected official test questions/audio without rights. Label content provenance and license.
9. No AI slop: prohibit excessive gradient, noisy chip bars, duplicated actions, fake activity/feed, unnecessary emojis, invented metrics, generic chat shortcuts above conversation.
10. 'Ponytail' is not a verified engineering methodology here: treat as a user preference for restrained, polished details; do not install an unverified package or invent a standard.

## Agent workflow — Spec Kit as authoritative pipeline, BMAD for supporting roles
- Discovery: inspect project, baseline, ADRs and existing tests before editing; audit requirements and licenses.
- Spec Kit: constitution -> specify -> plan -> tasks -> implement -> converge (use commands supported by installed version). Per feature create specs/<id>/spec.md, plan.md, tasks.md, acceptance.md.
- BMAD: product analyst reviews goals, architect reviews choices, UX reviews journeys, QA reviews test coverage. Store review notes in docs/reviews; BMAD does not create a second conflicting set of authoritative requirements.
- TDD where practical: failing tests -> minimal implementation -> refactor -> regression, followed by instrumented checks.
- Work in vertical slices; commit independently; document tradeoffs and actual test output, never fabricate pass status.
- Order: app skeleton -> course catalog -> question engine -> audio -> timed exam -> local progress -> AI model SAF import -> chat/RAG -> speaking/STT -> writing feedback -> polish.
- Prioritize correctness before performance claims; benchmark Gemma/Whisper by ABI, memory, storage, cold start and tokens/sec on target device.

## Expected Android stack
Kotlin, Jetpack Compose Material3, Navigation Compose, ViewModel, Coroutines/Flow, Hilt, Room, DataStore, Media3 ExoPlayer, Android AudioRecord/MediaRecorder, Storage Access Framework, Kotlin Serialization, JUnit, Turbine, Compose UI Test, Macrobenchmark when needed. AI inference adapter first; choose validated Gemma 4-compatible Android runtime and quantization only after device-specific proof-of-concept. Speech adapter can use a validated ONNX Runtime Mobile/Whisper implementation or equivalent open-source on-device engine.

## MCP and agent skills (optional, do not require paid APIs)
- Filesystem MCP; Git MCP; GitHub MCP (optional repo integration); Fetch MCP for documentation. Playwright MCP only for websites/doc viewers, NOT a substitute for Android UI tests.
- Skills: android-compose-design-review, accessibility-audit, android-lifecycle, media3-audio, room-migrations, saf-file-security, on-device-llm, on-device-stt, content-validation, test-design, performance-profiling, license-audit.
- Confirm each tool's maintained source, license, installation and permissions before using. Android tooling (Gradle/ADB/Emulator/Android Lint) is the main verification path.

## Required test matrix
- Quiz: MCQ, multi-select, matching, fill blank, ordering, typo normalization where permitted, answer changes, skipped items, resumption, corrupt manifest.
- Timing: process death, background/foreground, device clock changes, rotation; use monotonic elapsed duration during process and persisted wall-clock checkpoints cautiously.
- Media: pause/play/seek rules per mode, audio focus loss, Bluetooth, phone call, absent asset, invalid codec.
- SAF: one file and tree, granted read permission persistence, revoked permission, moved file, 0-byte file, hash mismatch, low storage; copy into app-private storage for random-access runtimes, after user consent.
- AI: model unsupported OOM, cancel generation, inference after rotation, offline test, prompt injection in imported study documents, incorrect answer propagation, model disposal.
- Exam: strict vs practice mode, score key reproducibility, score provenance, correct section-specific rules.

## Collaboration & command prompt
For each task: (1) cite relevant spec IDs; (2) list smallest files to change; (3) implement; (4) run actual tests; (5) report pass/fail evidence, remaining risks and next smallest task. No broad refactors without need.

## Definition of Done
- Semua alur utama berjalan sepenuhnya tanpa internet setelah aset diimpor.
- Build debug berhasil, unit/instrumented tests lulus; manual QA pada ponsel nyata.
- Tidak ada crash untuk SAF URI hilang, file corrupt, izin dicabut, low storage, audio focus, background/rotation.
- Aksesibilitas TalkBack, font scaling 200%, target sentuh ≥48dp, kontras dan reduced motion diperiksa.
- Tidak ada klaim nilai resmi, bank soal berhak cipta yang disalin, atau model yang dipalsukan.
- Dokumentasi, changelog, dan checklist verifikasi diperbarui berdasarkan hasil aktual.
