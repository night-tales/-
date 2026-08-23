import re

with open('app/src/main/java/com/example/data/local/AppDatabase.kt', 'r') as f:
    content = f.read()

# Add CharacterEntity to the entities list
content = content.replace(
    'entities = [ProjectEntity::class, SceneEntity::class, com.example.data.local.entity.GeneratedStoryEntity::class]',
    'entities = [ProjectEntity::class, SceneEntity::class, com.example.data.local.entity.GeneratedStoryEntity::class, com.example.data.local.entity.CharacterEntity::class]'
)
# Increment version
content = re.sub(r'version = \d+', 'version = 4', content)

# Add CharacterDao
if 'characterDao' not in content:
    content = content.replace(
        'abstract fun generatedStoryDao(): com.example.data.local.dao.GeneratedStoryDao',
        'abstract fun generatedStoryDao(): com.example.data.local.dao.GeneratedStoryDao\n    abstract fun characterDao(): com.example.data.local.dao.CharacterDao'
    )

with open('app/src/main/java/com/example/data/local/AppDatabase.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/di/DatabaseModule.kt', 'r') as f:
    mod_content = f.read()

if 'provideCharacterDao' not in mod_content:
    mod_content = mod_content.replace(
        '}\n}',
        '}\n\n    @Provides\n    fun provideCharacterDao(database: AppDatabase): com.example.data.local.dao.CharacterDao {\n        return database.characterDao()\n    }\n}'
    )

with open('app/src/main/java/com/example/di/DatabaseModule.kt', 'w') as f:
    f.write(mod_content)
