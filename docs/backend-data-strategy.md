# Backend and data-layer strategy

## Current state

Night Tales Studio currently uses Room/SQLite as its local source of truth. Firebase libraries are present, but Firestore and Firebase Authentication are not enabled in the app module.

## Decision

Keep Room for offline-first local editing and add a remote backend behind repository/data-source interfaces. Do not couple UI/ViewModels directly to Firebase, Supabase, or another provider.

For the current Android architecture, Firebase is the lowest-friction remote option because the project already includes the Firebase BOM, Google Services plugin, Firebase AI, and App Check. Firestore/Auth can be enabled later without replacing the local Room model.

## Provider options

| Provider | Role | Recommendation |
|---|---|---|
| Firebase Firestore + Auth | Remote document sync/auth | Preferred for the current app |
| Supabase | PostgreSQL + Auth + Storage + Realtime | Strong alternative if relational/cloud SQL becomes the primary model |
| Appwrite | Open-source backend + Auth + Database + Storage | Strong self-hosted alternative |
| Neon | PostgreSQL | Database-only option; requires separate Auth/API/Storage |

## Integration order

1. Keep Room as the offline cache/source for editing.
2. Introduce repository-level remote data-source interfaces.
3. Add authentication and a stable user/project ownership model.
4. Add Firestore synchronization with conflict handling and retry/backoff.
5. Keep generated media in object storage rather than the database.
6. Add sync tests before switching any screen to remote-backed data.

## Security rules

- Never put server credentials or privileged keys in the APK.
- Do not log HTTP request/response bodies in release builds.
- Treat AI prompts and generated content as user data.
- Use App Check/authentication and least-privilege backend rules once remote sync is enabled.

## Important limitation

A real Firestore/Auth integration requires the Firebase project configuration (google-services.json) and the intended authentication provider. Those credentials/configuration are intentionally not committed to the repository.