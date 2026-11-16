# BMAD Review — System Architect: Tech Choices & Modularity

- **Reviewer Role**: BMAD Architect
- **Authoritative Spec**: `ARSITEKTUR.md`, `DATA_SCHEMA.md`

## Architectural Decisions
1. **Single `:app` Module + Clean Package Boundaries**: `ui/feature/* -> domain/* <- data/*`. Pure Kotlin domain grading (`QuizGradingEngine`, `ExamScoreEstimator`, `SpeakingMetricsAnalyzer`) keeps zero Android dependencies for fast deterministic JVM testing.
2. **Persistence**: Room (`english_prep.db`, schema export ON) covers all 15 entities + `CurriculumFts` (FTS4) for offline grounded RAG retrieval. DataStore Preferences handles lightweight settings only.
3. **Media & Speech**: Media3 ExoPlayer (`1.5.1`) wrapped behind `AudioPlayer` with strict vs practice control gates, audio focus, and noisy-receiver protection. `AudioRecord` writes 16 kHz mono PCM WAV locally; `SpeechToTextEngine` integrates ONNX Runtime (`1.22.0`) for Whisper ONNX (`encoder_model.onnx`, `decoder_model.onnx`, `vocab.json`).
4. **Gemma 4 Offline LLM**: `LocalLlmEngine` wraps `com.google.ai.edge.litertlm:litertlm-android:0.13.1` (`Engine`, `Conversation`, `MessageCallback`, `cancelProcess()`) for verified `.litertlm` artifacts (e.g., `gemma-4-E2B-it.litertlm`). Never disguises Gemma 3 as Gemma 4.
5. **SAF Security**: `SafSecurityValidator` enforces magic headers, zero-byte rejection, zip-slip (`../`) blocking, decompression-bomb byte ratio caps, SHA-256 checksums, free disk checks, and atomic swaps into `files/models/` and `files/packs/`.
