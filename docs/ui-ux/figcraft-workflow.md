# FigCraft Design Workflow (`docs/ui-ux/figcraft-workflow.md`)

- **Tooling**: ArtCraft FigCraft MCP (`figcraft`) paired with Compose token parity in `ui/theme/` and live Android hardware verification (`mobile-mcp`).
- **Source Files**:
  - Design Variables JSON: `design/tokens.json`
  - FigCraft Project Binary: `design/EnglishPrepAI-Redesign.figcraft`
  - Visual Export Set (16 PNGs): `design/exports/00-foundations.png` .. `design/exports/15-states-accessibility.png`

## 16 Organized FigCraft Pages

| Page Index | Name | Export File | Purpose & Scope |
|---|---|---|---|
| `00` | `00 — Foundations` | `design/exports/00-foundations.png` | Color palettes, spacing rhythm (`4/8/12/16/24/32`), typography scale, radius tokens |
| `01` | `01 — Design System` | `design/exports/01-design-system.png` | Light (`WarmPaper`) vs Dark (`Slate`) role pairings and semantic containers |
| `02` | `02 — Components` | `design/exports/02-components.png` | Reusable component library (`Button/Primary`, `Button/Outlined`, `ExamSelector`, `AudioPlayerBar`, `ModuleRow`, `BottomNavBar`, `QuestionOption`, `AnswerInput`, `ProgressSummary`, `ModelItem`, `InlineFeedback`, `ErrorState`) |
| `03`..`14` | Screen Artboards | `03-home.png` .. `14-ai-tutor.png` | Light + Dark responsive mobile frames across Home, Learn, Practice, Listening, Reading, Writing (+ IME), Speaking, Results, Progress, Profile, Model Manager, and AI Tutor (+ IME) |
| `15` | `15 — States & Accessibility` | `design/exports/15-states-accessibility.png` | Loading/empty/error states, 2.0x font-scaling bounds, `48dp` target guidelines, Light & Dark contrast checks |

## Design-to-Code Loop

1. Capture real-device defects via `mobile-mcp` into `specs/ui-redesign/design-audit.md`.
2. Encode Light/Dark token collections (`ThemeColors`) and `SpacingAndRadius` variables inside `design/tokens.json` and `design/EnglishPrepAI-Redesign.figcraft`.
3. Assemble component primitives and all 12 screen frames + states using auto-layout constraints and variable bindings, inspect Compose code output (`inspect.code`), and export all 16 PNGs for visual QA.
4. Translate exact spacing, typography, inset ownership, and labels into Compose screens (`MainActivity.kt`, `AppComponents.kt`, `Home*`, `LearnScreens`, `PracticeScreen`, `ExamScreen`, `Results*`, `Progress*`, `ChatScreen`), keeping all offline business logic untouched.
5. Verify parity on device (`RRCN3008VYE`) across Dark, Light, and open-keyboard states.
