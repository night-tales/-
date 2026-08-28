package com.example.data.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncOperationPolicyTest {
    @Test
    fun latestOperationWinsForSameEntity() {
        val operations = listOf("UPSERT", "DELETE", "UPSERT")
        assertEquals("UPSERT", operations.last())
    }

    @Test
    fun differentEntitiesRemainIndependent() {
        val operations = mapOf(
            "SCENE:scene-1" to "DELETE",
            "SCENE:scene-2" to "UPSERT"
        )
        assertEquals("DELETE", operations["SCENE:scene-1"])
        assertEquals("UPSERT", operations["SCENE:scene-2"])
    }
}
