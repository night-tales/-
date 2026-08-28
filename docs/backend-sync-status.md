# Backend sync status

## Current architecture

Room is the local source of truth. Mutations enqueue a single operation per entity and schedule WorkManager when connectivity is available. The worker dispatches Project, Scene, Character, and Generated Story operations to Firestore.

## Safety guarantees

- Authenticated writes are required by the repository layer.
- Firestore rules scope project children to the parent project's owner.
- Queue ordering is deterministic.
- New operations coalesce older operations for the same entity.
- Failed operations retain attempt/error metadata and use WorkManager retry.

## Verification

GitHub Actions now runs lint, unit tests, and a debug APK build on pushes and pull requests.

A successful CI run is required before merging the branch. This environment can edit GitHub files but cannot execute the repository's Gradle build directly, so CI is the source of truth for compilation status.
