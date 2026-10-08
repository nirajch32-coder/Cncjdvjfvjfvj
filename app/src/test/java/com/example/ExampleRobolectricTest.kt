package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SaraRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SARA", appName)
    }

    @Test
    fun `verify default presets and repository initialization`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SaraRepository(context)

        val settings = repo.getSettings()
        assertNotNull(settings)
        assertEquals("sassy_girlfriend", settings.activePresetId)
        assertTrue(settings.voiceInputEnabled)
        assertTrue(settings.voiceOutputEnabled)
        assertTrue(settings.femaleVoice)

        val permissions = repo.getPermissions()
        assertTrue(permissions.any { it.id == "mic" })
        assertTrue(permissions.any { it.id == "accessibility" })

        val memories = repo.getMemories()
        assertTrue(memories.isNotEmpty())

        val history = repo.getChatHistory()
        assertTrue(history.isNotEmpty())
    }
}
