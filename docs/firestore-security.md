# Firestore security

Projects are scoped to the authenticated Firebase user through `ownerId`.

Required project fields:

- `ownerId`
- `createdAt`
- `updatedAt`

The mobile client must never use an admin/service-account credential. Deploy `firestore.rules` with the Firebase CLI and configure Authentication before enabling remote project sync in production.

## Conflict policy

`updatedAt` is the logical version used by the application. A future bidirectional sync implementation must compare versions before replacing a newer local record with an older remote record.

The current worker intentionally performs only queued local-to-remote writes; it does not silently overwrite local data from remote snapshots.
