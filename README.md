# BUILDLABS

> **"Intelligence That Gets Things Done."**
> Hark-style Personal AI Operating System & Autonomous Agent Platform.

BUILDLABS is a production-grade personal AI assistant that understands user context, remembers useful operational preferences, coordinates dedicated workspace projects, executes approved actions, and provides a foundation for autonomous cloud computer agents.

---

## Current Release: Phase 0 (Foundation) + Phase 1 (Core App) + Phase 2 (AI Brain)

### Implemented & Verified Capabilities:

1. **Original Brand Identity**:
   - Signature Dark Navy aesthetic (`#0B0F19`), slate surfaces (`#161F30`), crisp typography, electric blue (`#3B82F6`) and neon violet (`#8B5CF6`) cyber accents.
   - Custom adaptive launcher icon (`ic_launcher`) featuring the geometric glowing BUILDLABS badge.

2. **Phase 0 — Foundation & Architecture**:
   - Edge-to-edge Material 3 Jetpack Compose application.
   - Clean MVVM architecture with Repository pattern.
   - Centralized design tokens and localized safe-area handling (`WindowInsets.navigationBars`).

3. **Phase 1 — Core Workspace & Local Persistence**:
   - **Local SQLite Room Database**: `users_profiles`, `user_preferences`, `projects`, `conversations`, `messages`, `action_cards`, `memories`, and `tool_executions`.
   - **Home Dashboard**: Dynamic personalized greeting (time-of-day), current local date & time, main AI assistant entry card, active projects horizontal shelf, Action Cards with approvals, system health metrics.
   - **Projects Workspace**: Create new projects, filter by status (All/Active/Archived), review objectives, delete with confirmation, and open dedicated project chat threads.
   - **Actions Engine**: Action Cards with priority badges (High, Medium, Low), deadline chips, authorization reviews, and completion tracking.
   - **User Identity & Preferences**: Profile management, session persistence, and Supabase integration guidance.

4. **Phase 2 — AI Brain & Agent Orchestration**:
   - **Model Gateway**: Support for `gemini-2.5-flash` / `gemini-3.8-flash`.
   - **Streaming Chat Engine**: Real-time token streaming with live cursor, stop generation control, and copy response actions.
   - **Strict Tool Registry**:
     - `get_user_profile`: Returns user information (safe internal).
     - `list_projects`: Lists active workspace projects (safe internal).
     - `get_project_details`: Fetches specific project data (safe internal).
     - `create_project`: Creates new projects with authorization modal (gated).
     - `create_action_card`: Inserts actionable tasks into Home dashboard.
     - `search_conversations`: Finds conversations by keyword.
   - **High-Risk Operation Gating**: The model cannot execute privileged operations directly. The user must explicitly confirm via the `ToolApprovalDialog`.

5. **12 Master Blueprint Subsystems (Primary Navigation)**:
   - Home (`Operational`)
   - Chat (`Operational`)
   - Projects (`Operational`)
   - Actions (`Operational`)
   - Panels (`Phase 6 Planned`)
   - Specialties (`Phase 6 Planned`)
   - Scheduled Tasks (`Phase 7 Planned`)
   - Integrations (`Phase 4 Planned`)
   - Cloud Handoff (`Phase 5 Planned`)
   - Security Vault (`Phase 8 Planned`)
   - Notifications (`Operational`)
   - Settings (`Operational`)

---

## Configuration & Setup

### Gemini API Key Setup
To connect live Gemini responses:
1. Open **Settings** inside the app, enter your Gemini API key in the **GEMINI_API_KEY** field, and tap **Save Key**.
2. Or configure `GEMINI_API_KEY` in the AI Studio Secrets panel.
3. If no key is configured, the application displays a clear configuration banner without generating false AI claims.

### Supabase Remote Cloud Sync (Future Phases)
When ready to connect remote cloud sync, add the following to `.env`:
```bash
VITE_SUPABASE_URL=https://your-project.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key
```
