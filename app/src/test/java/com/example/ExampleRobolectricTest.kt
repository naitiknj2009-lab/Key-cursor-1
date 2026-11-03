package com.example

import android.content.Context
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import com.example.data.KeyCursorPreferences
import com.example.data.MiddleClickAction
import com.example.data.PointerStyle
import com.example.data.SpeedPreset
import com.example.engine.CursorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("KeyCursor", appName)
    }

    @Test
    fun `test KeyCursorPreferences default values and persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = KeyCursorPreferences(context)
        prefs.resetToDefaults()

        assertEquals(PointerStyle.CYBER_ARROW, prefs.pointerStyle)
        assertEquals(28, prefs.cursorSizeDp)
        assertEquals(SpeedPreset.NORMAL, prefs.speedPreset)
        assertTrue(prefs.diagonalEnabled)
        assertTrue(prefs.cursorShadow)
        assertFalse(prefs.swapLeftRightClick)
        assertEquals(MiddleClickAction.SYSTEM_BACK, prefs.middleClickAction)

        // Test mutating and saving
        prefs.cursorSizeDp = 42
        assertEquals(42, prefs.cursorSizeDp)

        prefs.pointerStyle = PointerStyle.PRECISION_DOT
        assertEquals(PointerStyle.PRECISION_DOT, prefs.pointerStyle)

        prefs.swapLeftRightClick = true
        assertTrue(prefs.swapLeftRightClick)

        prefs.middleClickAction = MiddleClickAction.NOTIFICATIONS
        assertEquals(MiddleClickAction.NOTIFICATIONS, prefs.middleClickAction)
    }

    @Test
    fun `test KeyCursorPreferences key display name formatting`() {
        assertEquals("Up Arrow", KeyCursorPreferences.getKeyDisplayName(KeyEvent.KEYCODE_DPAD_UP))
        assertEquals("Enter", KeyCursorPreferences.getKeyDisplayName(KeyEvent.KEYCODE_ENTER))
        assertEquals("Spacebar", KeyCursorPreferences.getKeyDisplayName(KeyEvent.KEYCODE_SPACE))
        assertEquals("A", KeyCursorPreferences.getKeyDisplayName(KeyEvent.KEYCODE_A))
        assertEquals("F1", KeyCursorPreferences.getKeyDisplayName(KeyEvent.KEYCODE_F1))
    }

    @Test
    fun `test CursorEngine boundary clamping`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = KeyCursorPreferences(context)
        val engine = CursorEngine(context, prefs) { _, _ -> }
        engine.updateScreenSize(1200, 1920)

        // Current coordinates must be strictly within bounds
        assertTrue(engine.currentX in 0f..1199f)
        assertTrue(engine.currentY in 0f..1919f)
    }

    @Test
    fun `test KeyCursorPreferences theme mode persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = KeyCursorPreferences(context)
        prefs.resetToDefaults()

        // Default is dark theme
        assertTrue(prefs.isDarkTheme)

        // Toggle to light mode
        prefs.isDarkTheme = false
        assertFalse(prefs.isDarkTheme)

        // Toggle back to dark mode
        prefs.isDarkTheme = true
        assertTrue(prefs.isDarkTheme)
    }
}
