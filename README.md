# Night Tales

Night Tales is an Android/Jetpack Compose story-generation studio.

## Generation architecture

```text
WorkManager
  -> AI scene planning
  -> Room scene persistence
  -> ImageProvider
  -> AudioProvider
  -> VideoAssembler
  -> Project.videoUri
```

Generation state is persisted in Room and background work is scheduled with WorkManager.

## Media providers

Media generation is dependency-injected through `ImageProvider`, `AudioProvider`, and `VideoAssembler`. The branch currently ships explicit `Unconfigured*` adapters so a missing production integration fails loudly instead of producing demo media or fake URLs.

Google currently documents Gemini 3.1 Flash Image for image generation, Gemini 3.1 Flash TTS for speech generation, and Veo 3.1 for video generation. Provider credentials must remain outside source control and be injected through the existing secrets configuration.

## Development

The PR remains draft until build, unit tests, database migrations, and production media adapters are validated.
