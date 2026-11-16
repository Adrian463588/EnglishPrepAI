# Acceptance Criteria: Offline AI, SAF Import & Speaking/Writing Coaching

- [x] `AC-03-A`: SAF import copies valid Gemma 4 `.litertlm` / Whisper ONNX / pack files atomically to app-private storage, persists metadata in Room `ModelInstall`, and survives app restart.
- [x] `AC-03-B`: Corrupt, 0-byte, low-storage, zip-slip, or revoked SAF URI fails gracefully with clear actionable recovery and zero crashes.
- [x] `AC-03-C`: Chat screen contains zero suggestion chip rows above messages, zero floating clutter, displays offline model state, cites local source IDs, cancels cleanly, and works offline (including graceful fallback without model).
- [x] `AC-03-D`: Speaking requests `RECORD_AUDIO` just-in-time, plays back local WAV, labels Whisper transcripts as auto-generated/editable, and labels AI rubric feedback as non-official estimates.
