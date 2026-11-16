# Tasks: Menu-by-Menu UI/UX Polish, Responsive Adaptive Layout & App Icon (`009`)

- [x] **T09-01**: Author Spec 009 (`specs/009-uiux-responsive-icon-polish/*`) & BMAD alignment review (`docs/reviews/bmad-uiux-responsive-icon.md`).
- [x] **T09-02**: Build `tools/generate_app_icon.py` to generate Android adaptive vector icon (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`, `ic_launcher_monochrome.xml`, `mipmap-anydpi-v26/*`) + high-DPI raster `mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher*.png` + `docs/screenshots/app-icon-512.png`; visually verify icon output.
- [x] **T09-03**: Upgrade shared components in `AppComponents.kt` (`ResponsiveScreenContainer`, `rememberIsExpandedWidth`, tabular numeral `.tabular()` helper, `SelectableOptionRow` press micro-interaction, `AudioPlayerBar` pill highlights & ellipsis).
- [x] **T09-04**: Polish `HomeScreen.kt` (responsive 2-col Expanded layout, `FlowRow` Hero CTAs at `2.0x`, `skipPartiallyExpanded` switcher) & `PracticeScreen.kt` (responsive 2-col pack grid at `840dp+`).
- [x] **T09-05**: Fix `LearnViewModel.kt` + polish `LearnScreens.kt` (mutually-exclusive `Semua`/`Tersimpan` filter chips, smooth animated `MaterialBankRow`, responsive measure, `TopAppBar` ellipsis).
- [x] **T09-06**: Polish `ExamScreen.kt` (`tnum` timer, thin progress strip, wrapping bottom action bar for `320dp @ 2.0x`, `skipPartiallyExpanded` passage modal, compact navigator legend).
- [x] **T09-07**: Polish `ResultsScreen.kt` (result filter chips across 85-140 items + responsive max width) & `ChatScreen.kt` (ellipsis title, smooth auto-scroll, asymmetric bubbles).
- [x] **T09-08**: Polish `ProgressScreen.kt` (`fillMaxWidth` section card fix, animated bars, Expanded 2-col summary+trend) & `ProfileScreen.kt` (updated READY/OER copy, full-row `toggleable` switches, responsive container).
- [x] **T09-09**: Expand `UiAndAccessibilityTest.kt` for narrow `320dp @ 2.0x`, wide `840dp` tablet, filter chips, full-row toggles, and updated copy; pass `testDebugUnitTest` (`40/40`), `lintDebug` (`0` errors, `76` warnings), `assembleDebug`, `assembleRelease`.
- [x] **T09-10**: Verify `v1.1.1` R8 APK + new app icon & menus on physical `SM-G988B`, capture refreshed screenshots, update `VERIFICATION_CHECKLIST.md`, `EPIC_STATUS.md`, `CHANGELOG.md`, `README.md`, and push + publish GitHub Release `v1.1.1`.
