# Acceptance Criteria (`009-uiux-responsive-icon-polish`)

- **Given** a narrow `320dp` phone at `2.0x` font scale on `HomeScreen`, `ExamScreen` bottom bar, `LearnScreen`, and `ProfileScreen`:
  - **Then** every button, chip, and toggle row stays visible with >= `48dp` minimum height, zero text clipping/overlap, and zero horizontal scroll.
- **Given** an `840dp+` tablet/landscape screen:
  - **Then** content rows center within clean readable measure (`760dp-960dp`), `PracticeScreen` and `ProgressScreen` utilize two-column balance cleanly, and `ExamScreen` shows passage + questions side-by-side.
- **Given** `LearnScreen`:
  - **When** the user switches from `Tersimpan` to a section filter chip (e.g., `Listening`):
  - **Then** `bookmarkedOnly` resets automatically and `Semua` is not highlighted simultaneously with `Tersimpan`.
- **Given** `ResultsScreen` on a completed 140-item pack:
  - **Then** filter chips (`Semua`, `Salah`, `Dilewati`, `Benar`) filter item review cards cleanly in 1 tap.
- **Given** Android home launcher / package resources (`mipmap-anydpi-v26`, `mipmap-*`):
  - **Then** crisp bespoke Open Folio + Sound Wave + Nib icon assets load across all densities (`mdpi`..`xxxhdpi` + vector adaptive foreground/background/monochrome).
