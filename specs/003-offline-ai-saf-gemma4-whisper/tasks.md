# Tasks: Offline AI, SAF Import & Speaking/Writing Coaching

- [x] T020 Write failing unit tests for `SafSecurityValidator` (0-byte, corrupt header, zip traversal `../`, decompression bomb, low disk, SHA-256 verification, model format compatibility)
- [x] T021 Write failing unit tests for `CurriculumRagRetriever` (FTS retrieval citations, prompt-injection stripping/isolation) & `SpeakingMetricsAnalyzer` (WPM, filler count, duration) & `WhisperMelExtractor` (16kHz PCM framing/STFT shape `[1,80,3000]`)
- [x] T022 Implement `SafSecurityValidator`, `SafModelPackImporter`, and `ModelRepositoryImpl`
- [x] T023 Implement `LocalLlmEngine` + `LiteRtLmGemma4Engine` (`litertlm-android:0.13.1`) + `CurriculumRagRetriever`
- [x] T024 Implement `SpeechToTextEngine` + `WhisperOnnxSpeechEngine` (`onnxruntime-android:1.22.0`) + `WavAudioRecorder` + `SpeakingMetricsAnalyzer`
- [x] T025 Implement `RubricFeedbackAnalyzer` (Writing/Speaking offline LLM + deterministic structural fallback)
- [x] T026 Implement `ChatRoute`/`ChatScreen`, Speaking/Writing UI workflows, and `Profile -> Offline AI` Hugging Face catalog + SAF import controls
