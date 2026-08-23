with open('app/src/main/java/com/example/data/local/dao/SceneDao.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'suspend fun updateScene(scene: SceneEntity)',
    'suspend fun updateScene(scene: SceneEntity)\n\n    @Query("SELECT * FROM scenes WHERE id = :id")\n    suspend fun getSceneById(id: String): SceneEntity?\n\n    @Insert(onConflict = OnConflictStrategy.REPLACE)\n    suspend fun insertScene(scene: SceneEntity)'
)

with open('app/src/main/java/com/example/data/local/dao/SceneDao.kt', 'w') as f:
    f.write(content)
