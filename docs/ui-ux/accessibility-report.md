# Accessibility Verification Report (`docs/ui-ux/accessibility-report.md`)

- **Test Suite Scope**: `UiAndAccessibilityTest.kt` (`21` Robolectric Compose UI & Accessibility tests) + physical Samsung Galaxy S20 Ultra (`RRCN3008VYE`, Android 13, `1080x2400`) UIAutomator / screenshot inspection.

## Checklist of Verified Accessibility Properties

| Requirement | Implementation & Verification | Result |
|---|---|---|
| **Touch Targets >= 48dp** | Enforced via `Modifier.defaultMinSize(minHeight = AppSpacing.minTouchTarget)` (`48.dp`) on `AppButton`, `SelectableOptionRow`, `ExamSelector`, Exam bottom bar buttons (`Kembali`, `Periksa`, `Lanjut`, `Selesai`), Writing `Panduan Rubrik`, Passage trigger banner, and Profile rows. Verified by Robolectric assertions at `320dp`, `360dp`, and `2.0x` font scaling. | **PASS** |
| **Font Scaling at 2.0x** | `UiAndAccessibilityTest` tests Home and narrow `320dp` Exam bottom bar inside `CompositionLocalProvider(LocalDensity provides Density(baseDensity.density, 2.0f))` with automatic wrapping / `compactWrapBottomBar` (`FlowRow`) fallback so labels never clip. | **PASS** |
| **TalkBack Semantics Merging** | `ExamSelector` merges descendant nodes (`semantics(mergeDescendants = true) { role = Role.Button; contentDescription = "Ganti jenis ujian (${activeExam.title})" }`), chevron sets `contentDescription = null`; section headings carry `semantics { heading() }`; all decorative icons use `contentDescription = null`. | **PASS** |
| **System Bar & Contrast (Light & Dark)** | Warm Paper `#FAF9F6` / Ink `#1A1D1D` (Light) and Dark Slate `#111415` / Ink `#E8ECEB` (Dark) with contrast-matched `isAppearanceLightStatusBars` and `isAppearanceLightNavigationBars`. Verified live in both Dark and Light captures on `SM-G988B`. | **PASS** |
| **Keyboard (IME) Navigation** | Excluded `WindowInsets.ime` duplication across root and nested Scaffolds; `ChatScreen` and `ExamScreen` (`Writing` / `FILL_BLANK`) keep focused `OutlinedTextField`s and action bars visible right above open keyboard. | **PASS** |
| **Reduced Motion** | `EnglishPrepTheme(reduceMotion = true)` sets `MotionTokens` transitions to `EnterTransition.None` / `ExitTransition.None` and collapses feedback/chart animation durations to `0ms`. Covered in `UiAndAccessibilityTest` (`ExamScreen`, Navigator, Results, `ProgressScreen`). | **PASS** |
| **Screen Width Adaptivity** | Verified across `320dp`, `360dp`, `412dp` compact widths and `w960dp-h720dp` tablet width (`NavigationRail`, dual-column Practice grid, side-by-side Reading Passage + Question pane). | **PASS** |
