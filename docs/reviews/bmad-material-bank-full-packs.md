# BMAD Review — Material Bank & Full Blueprint Test Packs (`Spec 008`)

- **Reviewers**: BMAD Product Analyst, Architect, UX Lead, QA Engineer
- **Authoritative Spec**: `specs/008-material-bank-full-packs/spec.md`
- **Date**: 2026-11-16

## Perspective Notes
1. **Product Analyst (Goal & Legal Boundary)**:
   - Addresses user gap directly: elevates sample-only `12`-item packs to full exam simulations (`140` ITP Level 1, `120` iBT 2026, `85` IELTS Academic = `1,725` items across 15 packs) and creates a verifiable `material_bank/` sourced via deep PDF search/dorking.
   - Enforces `AGENTS.md` Rule 8 strictly: separates CC-BY/OER redistributable textbooks & public academic PDFs (`material_bank/open_oer_pdfs/`) from copyrighted official test/handbook PDFs (`material_bank/official_reference_pdfs/` gitignored with traceable metadata only in manifest); all test items remain original or OER-grounded with clear `license` and `provenance`.
2. **Architect (APK Footprint, Upgrades & DRY)**:
   - Uncompressed 16-bit PCM WAV across 15 full exams would add ~144 MB; switching speech tracks to standard RIFF 16 kHz 4-bit MS IMA-ADPCM (`WAVE_FORMAT_DVI_ADPCM = 0x0011`, supported directly by Media3 `WavExtractor$ImaAdPcmOutputWriter`) cuts audio weight ~4x while keeping full 16 kHz intelligibility and `"RIFF"` header compatibility.
   - `AssetSeeder` moves from naive `countExamProfiles() >= 3` short-circuit to versioned transactional `@Upsert` (`BUNDLED_CONTENT_VERSION`), protecting existing users' saved sessions/answers/AI models during upgrades.
   - Python content generators follow modular DRY helpers under `tools/` with deterministic pre-emission assertions.
3. **UX Lead (Full-Test Ergonomics)**:
   - Navigating `140` items via an unscrollable `FlowRow` clips off-screen; upgraded Navigator Bottom Sheet adds section filter tabs and chunked grid scrolling with 48dp touch targets.
   - Shared-audio tracks (ITP Part B/C, iBT Conversations/Talks, IELTS Parts 1–4) must not reset playback or consume Strict replay limits when advancing between consecutive questions attached to the same recording.
   - In-app Material Bank documents appear cleanly inside `LearnScreens` using existing Material 3 typography cards without heavy external PDF dependencies or card nesting slop.
4. **QA Engineer (Honesty & Verification Gates)**:
   - Closes `E04-08`, `E06-07`, `E07-02`, `E07-04` pack completion software/item-count gates once automated integrity checks (`140`/`120`/`85` items, unique IDs/prompts, resolved audio/passages) and device playback QA pass, while retaining honest caveats for human psychometric/native-speaker calibration, external SAF `.onnx`/`.litertlm` weights (`E10-05`, `E17-04/05`), and Switch Access (`E21-04`).
