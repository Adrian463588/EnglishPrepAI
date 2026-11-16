# BMAD Review — UX Designer: Tap Depth, Anti-AI-Slop & Micro-Interactions

- **Reviewer Role**: BMAD UX Reviewer
- **Authoritative Spec**: `DESIGN.md`, `android-compose-anti-slop`

## UX & Accessibility Audit Criteria
1. **Navigation Discipline**: 5 top-level destinations (`Home`, `Learn`, `Practice`, `Progress`, `Profile`) rendered via `NavigationBar` on Compact (<600dp) and `NavigationRail` on Medium/Expanded (>=600dp).
2. **Tap Budget Verification**:
   - Home -> Resume primary CTA: **1 tap**
   - Home -> Exam switcher -> Select exam: **2 taps**
   - Home -> AI Tutor chat: **1 tap** (discrete card/header button, zero floating FAB over exam screens)
   - Home -> Practice -> Start Mock Package: **2 taps** (Practice tab -> Start) or **3 taps** with mode dialog
   - Home -> Learn -> Select Topic -> Open Lesson: **3 taps**
3. **Anti-AI-Slop Guardrails**: Warm neutral surface (`#FAF9F6` light / `#121516` dark) with single deep-teal accent (`#0F6B66`). No multi-color gradients, no fake streak banners, no decorative uppercase eyebrows, no chip rows above Chat messages.
4. **Micro-Interactions & Fine UX**:
   - Subtle spring/tween state feedback (120–200ms) bypassed cleanly when `reduceMotion` is enabled.
   - Quiet inline `"Saved"` status timestamp in Exam & Writing screens; optional gentle haptic confirmation on submit.
   - Keyboard/IME handling via `imePadding()` + `WindowInsets.safeDrawing` in Writing and Chat screens.
   - Minimum `48dp` interactive bounds, `>=8dp` spacing, TalkBack `heading()` roles, and verified `2.0x` font scaling.
