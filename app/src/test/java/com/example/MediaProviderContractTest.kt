package com.example

import com.example.generation.UnconfiguredAudioProvider
import com.example.generation.UnconfiguredImageProvider
import com.example.generation.UnconfiguredVideoAssembler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class MediaProviderContractTest {
    @Test
    fun imageProviderFailsExplicitlyWhenNotConfigured() = runTest {
        assertThrows(IllegalStateException::class.java) {
            runTest { UnconfiguredImageProvider().generate("test prompt") }
        }
    }

    @Test
    fun audioProviderFailsExplicitlyWhenNotConfigured() = runTest {
        assertThrows(IllegalStateException::class.java) {
            runTest { UnconfiguredAudioProvider().synthesize("test") }
        }
    }

    @Test
    fun videoAssemblerFailsExplicitlyWhenNotConfigured() = runTest {
        assertThrows(IllegalStateException::class.java) {
            runTest { UnconfiguredVideoAssembler().assemble(emptyList()) }
        }
    }
}
