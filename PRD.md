# PRD — English Prep AI
Status: implementation-ready specification, not shipped product. Version 2026-11-16.

## Product vision
A privacy-respecting offline Android study companion in Bahasa Indonesia/English for TOEFL ITP Level 1, TOEFL iBT 2026 and IELTS Academic, with trustworthy deterministic mock-test grading and optional small local AI tutor.

## Personas and outcomes
- Student with limited internet: download/import content once, keep practicing offline.
- Working learner: resume a 10–20 minute module quickly.
- Test candidate: finish timed mock tests, review mistakes and track weak skills.
- Advanced learner: practice Writing/Speaking and receive clearly qualified offline AI feedback.

## Screens / core journeys
Home -> select exam -> (Study | Practice | Mock Test) -> select section/module/package -> attempt -> results -> explanation -> retry mistakes.
Bottom navigation only: Home, Learn, Practice, Progress, Profile; contextual exam selector persisted. Chatbot entry as a single discrete action in Home / learning detail, never floating clutter over exam tasks; within Chat screen use clean thread and composer, no suggestion chips above messages. All downloads/model settings and reminder settings consolidated under Profile.

## Features and priorities
P0: exams catalog, curriculum, browse topics, objective question types, seeded original practice, Media3 offline playback, strict/practice timer, auto-save session, explanations, Room history, analytics, export backup, SAF imports, accessible UI.
P1: Gemma 4 local chatbot, local passage-grounded retrieval, import validated quantized model, on-device text evaluation for IELTS/iBT Writing, voice recorder and Whisper-compatible offline STT, speaking feedback.
P2: study reminders, optional external downloads through system browser, user-created packs, adaptive practice from errors, TTS production, polished exam-like IELTS interaction, General Training content as separate future exam variant.

## Test modes and validation
ITP Level 1: Listening 50/35min; Structure+Written Expression 40/25min; Reading 50/55min; 140 total/115min; score scale 310–677. True converted score requires authentic ETS conversion tables; without licensed tables show raw correct and clearly labeled non-official prediction only.
iBT since 2026-01-21: Reading approximately 50 tasks/30min; Listening ~47/29min; Writing ~12/23min; Speaking ~11/8min; adaptive test content and timing can vary. Format task catalog: Complete the Words, Read in Daily Life, Read an Academic Passage; Listen and Choose a Response/Conversation/Announcement/Academic Talk; Build a Sentence/Write an Email/Academic Discussion; Listen and Repeat/Take an Interview. Official overall scale 1–6; comparable 0–120 in transition. NEVER claim a fixed local linear formula matches official adaptive scoring. Show practice raw metrics + optional uncalibrated estimated proficiency.
IELTS Academic: Listening 40 (~30min, 4 parts), Reading 40 (60min, 3 passages), Writing 2 tasks (60min), Speaking 3 parts (11–14min); overall band 0–9. For writing/speaking show rubric-aligned AI coaching, not an official predicted band unless calibrated and transparent.

## Five packages per exam
15 package manifest IDs: ITP-L1-01..05, IBT26-01..05, IELTS-A-01..05, as detailed in packs/index.json. Each has distinct theme, blueprint counts, durations, source/license, QA status. P0 demonstrator has 1+ original sample questions per package; complete full-length packages are content-production backlog, never label complete until item-count checks pass. User may import third-party legitimately licensed full packs.

## Curriculum
Complete hierarchy and learning objectives in content/CURRICULUM.md. Each module contains theory, worked example, drills, mistake explanation, difficulty, estimated duration, source trace, optional audio/script. Schedule: diagnostic -> targeted units -> sectional mocks -> full-length mock -> mistake review.

## Nonfunctional requirements
- Core fully usable without network once content and models are installed.
- Cold start target <3 seconds on midrange phone (measure, do not promise).
- Preserve answers after interruption; no loss on rotation/process recovery.
- Use stable IDs and database migrations. Audio begins only on explicit action or according to exam instructions; no autoplay surprises in practice mode.
- Screen-reader labels, contrast >= WCAG AA where feasible, 48dp touch targets, font scaling up to 200%, reduced-motion support.
- APK size target <80MB excluding imported model/audio (nonbinding until measured); validate storage requirements before model import.
- AI supports graceful degradation if no model loaded or RAM insufficient; objective exams always work.

## Acceptance examples
AC-01: airplane mode: browse imported course, answer questions, play local audio, obtain reproducible score.
AC-02: revoked SAF permission: understandable error + re-link action, no crash or silent data deletion.
AC-03: model incompatible: show model runtime/format/ABI mismatch and suggested compatible import.
AC-04: 15 package cards exist but incomplete packages visibly say 'Sample / Content pending', never impersonate a full test.
AC-05: exam questions maintain scroll/selection, do not show explanations until submitted in strict mode.
AC-06: AI-disabled experience never blocks quiz, playback, and statistics.
AC-07: results list objective key + explanation + corresponding lesson; writing/speaking evaluation shows method/limitations.

## Out of scope
Official ETS/British Council endorsement; protected licensed question duplication; exact official adaptive scoring replication; online login, cloud sync, payment, social feeds, remote AI inference, and anti-cheat surveillance.

## Sources
https://www.ets.org/toefl/itp/test-content.html
https://www.ets.org/toefl/test-takers/ibt/about/content.html?version=after-jan-2026
https://ielts.org/take-a-test/test-types/ielts-academic-test

## Definition of Done
- Semua alur utama berjalan sepenuhnya tanpa internet setelah aset diimpor.
- Build debug berhasil, unit/instrumented tests lulus; manual QA pada ponsel nyata.
- Tidak ada crash untuk SAF URI hilang, file corrupt, izin dicabut, low storage, audio focus, background/rotation.
- Aksesibilitas TalkBack, font scaling 200%, target sentuh ≥48dp, kontras dan reduced motion diperiksa.
- Tidak ada klaim nilai resmi, bank soal berhak cipta yang disalin, atau model yang dipalsukan.
- Dokumentasi, changelog, dan checklist verifikasi diperbarui berdasarkan hasil aktual.
