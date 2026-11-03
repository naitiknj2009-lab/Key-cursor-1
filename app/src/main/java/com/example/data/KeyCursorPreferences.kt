package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.view.KeyEvent

enum class PointerStyle(val displayName: String, val description: String) {
    CYBER_ARROW("Cyber Glow Arrow", "Faceted neon pointer with luminous accent"),
    TRADITIONAL_ARROW("Classic Mouse", "Traditional desktop OS cursor with drop shadow"),
    PRECISION_DOT("Precision Reticle", "Minimalist targeting dot with outer concentric ring"),
    CROSSHAIR("Target Crosshair", "High-precision crosshair reticle for fine coordinate alignment")
}

enum class SpeedPreset(val displayName: String, val multiplier: Float) {
    SLOW("Slow (0.5x)", 0.5f),
    NORMAL("Normal (1.0x)", 1.0f),
    FAST("Fast (2.0x)", 2.0f)
}

enum class DragTriggerMode(val displayName: String) {
    HOLD_SPACE("Hold Key to Drag"),
    TOGGLE_SPACE("Tap Key to Toggle Drag")
}

enum class MiddleClickAction(val displayName: String, val description: String) {
    SYSTEM_BACK("System Back Button", "Navigates back to previous app or screen"),
    SYSTEM_HOME("System Home Button", "Returns to Android launcher home screen"),
    NOTIFICATIONS("Open Notifications", "Pulls down notification and quick settings shade"),
    RECENTS("App Overview / Recents", "Displays Android recent apps switcher"),
    DOUBLE_CLICK("Double Click Tap", "Dispatches rapid double-tap at cursor position")
}

/**
 * Manages local SharedPreferences persistence for KeyCursor settings and mouse controls.
 * All settings are stored locally on-device with zero telemetry or network calls.
 */
class KeyCursorPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("key_cursor_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURSOR_ENABLED = "cursor_enabled"
        private const val KEY_POINTER_STYLE = "pointer_style"
        private const val KEY_CURSOR_SIZE_DP = "cursor_size_dp"
        private const val KEY_CURSOR_COLOR = "cursor_color"
        private const val KEY_CURSOR_OPACITY = "cursor_opacity"
        private const val KEY_CURSOR_SHADOW = "cursor_shadow"
        private const val KEY_CURSOR_VISIBILITY = "cursor_visibility"
        private const val KEY_CLICK_ANIMATION = "click_animation"
        private const val KEY_DRAG_ANIMATION = "drag_animation"

        private const val KEY_BASE_SPEED = "base_speed"
        private const val KEY_ACCELERATION = "acceleration"
        private const val KEY_SPEED_PRESET = "speed_preset"
        private const val KEY_FAST_MULTIPLIER = "fast_multiplier"
        private const val KEY_FINE_MULTIPLIER = "fine_multiplier"
        private const val KEY_DIAGONAL_ENABLED = "diagonal_enabled"

        private const val KEY_CLICK_DURATION = "click_duration"
        private const val KEY_DOUBLE_CLICK_ENABLED = "double_click_enabled"
        private const val KEY_DOUBLE_CLICK_INTERVAL = "double_click_interval"
        private const val KEY_LONG_PRESS_DURATION = "long_press_duration"
        private const val KEY_DRAG_MODE = "drag_mode"
        private const val KEY_SCROLL_STEP = "scroll_step"
        private const val KEY_SCROLL_SPEED = "scroll_speed"
        private const val KEY_SWAP_LEFT_RIGHT = "swap_left_right"
        private const val KEY_INVERT_SCROLL = "invert_scroll"
        private const val KEY_MIDDLE_CLICK_ACTION = "middle_click_action"

        // Keyboard mapping
        private const val KEY_MAPPING_UP = "key_map_up"
        private const val KEY_MAPPING_DOWN = "key_map_down"
        private const val KEY_MAPPING_LEFT = "key_map_left"
        private const val KEY_MAPPING_RIGHT = "key_map_right"
        private const val KEY_MAPPING_CLICK = "key_map_click"
        private const val KEY_MAPPING_RIGHT_CLICK = "key_map_right_click"
        private const val KEY_MAPPING_MIDDLE_CLICK = "key_map_middle_click"
        private const val KEY_MAPPING_DOUBLE_CLICK = "key_map_double_click"
        private const val KEY_MAPPING_DRAG = "key_map_drag"
        private const val KEY_MAPPING_SCROLL_UP = "key_map_scroll_up"
        private const val KEY_MAPPING_SCROLL_DOWN = "key_map_scroll_down"
        private const val KEY_MAPPING_FAST = "key_map_fast"
        private const val KEY_MAPPING_FINE = "key_map_fine"
        private const val KEY_MAPPING_TOGGLE = "key_map_toggle"

        // Floating Panel
        private const val KEY_PANEL_ENABLED = "panel_enabled"
        private const val KEY_PANEL_COMPACT = "panel_compact"
        private const val KEY_PANEL_ALPHA = "panel_alpha"
        private const val KEY_PANEL_X = "panel_x"
        private const val KEY_PANEL_Y = "panel_y"

        // Automation & Positions
        private const val KEY_AUTO_ACTIVATE = "auto_activate_on_connect"
        private const val KEY_AUTO_DEACTIVATE = "auto_deactivate_on_disconnect"
        private const val KEY_INACTIVITY_TIMEOUT = "inactivity_timeout_seconds"
        private const val KEY_LAST_X = "last_cursor_x"
        private const val KEY_LAST_Y = "last_cursor_y"
        private const val KEY_DARK_THEME = "dark_theme"

        fun getKeyDisplayName(keyCode: Int): String {
            return when (keyCode) {
                KeyEvent.KEYCODE_UNKNOWN -> "None / Unassigned"
                KeyEvent.KEYCODE_DPAD_UP -> "Up Arrow"
                KeyEvent.KEYCODE_DPAD_DOWN -> "Down Arrow"
                KeyEvent.KEYCODE_DPAD_LEFT -> "Left Arrow"
                KeyEvent.KEYCODE_DPAD_RIGHT -> "Right Arrow"
                KeyEvent.KEYCODE_ENTER -> "Enter"
                KeyEvent.KEYCODE_NUMPAD_ENTER -> "Numpad Enter"
                KeyEvent.KEYCODE_SPACE -> "Spacebar"
                KeyEvent.KEYCODE_TAB -> "Tab"
                KeyEvent.KEYCODE_ESCAPE -> "Escape"
                KeyEvent.KEYCODE_PAGE_UP -> "Page Up"
                KeyEvent.KEYCODE_PAGE_DOWN -> "Page Down"
                KeyEvent.KEYCODE_MOVE_HOME -> "Home"
                KeyEvent.KEYCODE_MOVE_END -> "End"
                KeyEvent.KEYCODE_SHIFT_LEFT -> "Left Shift"
                KeyEvent.KEYCODE_SHIFT_RIGHT -> "Right Shift"
                KeyEvent.KEYCODE_CTRL_LEFT -> "Left Ctrl"
                KeyEvent.KEYCODE_CTRL_RIGHT -> "Right Ctrl"
                KeyEvent.KEYCODE_ALT_LEFT -> "Left Alt"
                KeyEvent.KEYCODE_ALT_RIGHT -> "Right Alt"
                KeyEvent.KEYCODE_CAPS_LOCK -> "Caps Lock"
                KeyEvent.KEYCODE_NUM_LOCK -> "Num Lock"
                KeyEvent.KEYCODE_INSERT -> "Insert"
                KeyEvent.KEYCODE_FORWARD_DEL -> "Delete"
                KeyEvent.KEYCODE_DEL -> "Backspace"
                KeyEvent.KEYCODE_BUTTON_R1 -> "Shift + Enter / R1"
                in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z -> {
                    ('A' + (keyCode - KeyEvent.KEYCODE_A)).toString()
                }
                in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                    ('0' + (keyCode - KeyEvent.KEYCODE_0)).toString()
                }
                in KeyEvent.KEYCODE_F1..KeyEvent.KEYCODE_F12 -> {
                    "F" + (keyCode - KeyEvent.KEYCODE_F1 + 1)
                }
                else -> KeyEvent.keyCodeToString(keyCode).removePrefix("KEYCODE_")
            }
        }
    }

    var isCursorEnabled: Boolean
        get() = prefs.getBoolean(KEY_CURSOR_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_CURSOR_ENABLED, value).apply()

    var pointerStyle: PointerStyle
        get() {
            val name = prefs.getString(KEY_POINTER_STYLE, PointerStyle.CYBER_ARROW.name)
            return try {
                PointerStyle.valueOf(name ?: PointerStyle.CYBER_ARROW.name)
            } catch (e: Exception) {
                PointerStyle.CYBER_ARROW
            }
        }
        set(value) = prefs.edit().putString(KEY_POINTER_STYLE, value.name).apply()

    var cursorSizeDp: Int
        get() = prefs.getInt(KEY_CURSOR_SIZE_DP, 28)
        set(value) = prefs.edit().putInt(KEY_CURSOR_SIZE_DP, value.coerceIn(16, 64)).apply()

    var cursorColor: Long
        get() = prefs.getLong(KEY_CURSOR_COLOR, 0xFF00E5FF)
        set(value) = prefs.edit().putLong(KEY_CURSOR_COLOR, value).apply()

    var cursorOpacity: Float
        get() = prefs.getFloat(KEY_CURSOR_OPACITY, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_CURSOR_OPACITY, value.coerceIn(0.2f, 1.0f)).apply()

    var cursorShadow: Boolean
        get() = prefs.getBoolean(KEY_CURSOR_SHADOW, true)
        set(value) = prefs.edit().putBoolean(KEY_CURSOR_SHADOW, value).apply()

    var cursorVisibility: Boolean
        get() = prefs.getBoolean(KEY_CURSOR_VISIBILITY, true)
        set(value) = prefs.edit().putBoolean(KEY_CURSOR_VISIBILITY, value).apply()

    var clickAnimation: Boolean
        get() = prefs.getBoolean(KEY_CLICK_ANIMATION, true)
        set(value) = prefs.edit().putBoolean(KEY_CLICK_ANIMATION, value).apply()

    var dragAnimation: Boolean
        get() = prefs.getBoolean(KEY_DRAG_ANIMATION, true)
        set(value) = prefs.edit().putBoolean(KEY_DRAG_ANIMATION, value).apply()

    var baseSpeed: Float
        get() = prefs.getFloat(KEY_BASE_SPEED, 16f)
        set(value) = prefs.edit().putFloat(KEY_BASE_SPEED, value.coerceIn(4f, 40f)).apply()

    var acceleration: Float
        get() = prefs.getFloat(KEY_ACCELERATION, 1.6f)
        set(value) = prefs.edit().putFloat(KEY_ACCELERATION, value.coerceIn(1.0f, 3.5f)).apply()

    var speedPreset: SpeedPreset
        get() {
            val name = prefs.getString(KEY_SPEED_PRESET, SpeedPreset.NORMAL.name)
            return try {
                SpeedPreset.valueOf(name ?: SpeedPreset.NORMAL.name)
            } catch (e: Exception) {
                SpeedPreset.NORMAL
            }
        }
        set(value) = prefs.edit().putString(KEY_SPEED_PRESET, value.name).apply()

    var fastMultiplier: Float
        get() = prefs.getFloat(KEY_FAST_MULTIPLIER, 2.4f)
        set(value) = prefs.edit().putFloat(KEY_FAST_MULTIPLIER, value.coerceIn(1.5f, 4.0f)).apply()

    var fineMultiplier: Float
        get() = prefs.getFloat(KEY_FINE_MULTIPLIER, 0.35f)
        set(value) = prefs.edit().putFloat(KEY_FINE_MULTIPLIER, value.coerceIn(0.1f, 0.7f)).apply()

    var diagonalEnabled: Boolean
        get() = prefs.getBoolean(KEY_DIAGONAL_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_DIAGONAL_ENABLED, value).apply()

    var clickDurationMs: Long
        get() = prefs.getLong(KEY_CLICK_DURATION, 60L)
        set(value) = prefs.edit().putLong(KEY_CLICK_DURATION, value.coerceIn(30L, 200L)).apply()

    var doubleClickEnabled: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_CLICK_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_DOUBLE_CLICK_ENABLED, value).apply()

    var doubleClickIntervalMs: Long
        get() = prefs.getLong(KEY_DOUBLE_CLICK_INTERVAL, 300L)
        set(value) = prefs.edit().putLong(KEY_DOUBLE_CLICK_INTERVAL, value.coerceIn(150L, 600L)).apply()

    var longPressDurationMs: Long
        get() = prefs.getLong(KEY_LONG_PRESS_DURATION, 500L)
        set(value) = prefs.edit().putLong(KEY_LONG_PRESS_DURATION, value.coerceIn(300L, 1200L)).apply()

    var dragTriggerMode: DragTriggerMode
        get() {
            val name = prefs.getString(KEY_DRAG_MODE, DragTriggerMode.HOLD_SPACE.name)
            return try {
                DragTriggerMode.valueOf(name ?: DragTriggerMode.HOLD_SPACE.name)
            } catch (e: Exception) {
                DragTriggerMode.HOLD_SPACE
            }
        }
        set(value) = prefs.edit().putString(KEY_DRAG_MODE, value.name).apply()

    var scrollStepPx: Int
        get() = prefs.getInt(KEY_SCROLL_STEP, 280)
        set(value) = prefs.edit().putInt(KEY_SCROLL_STEP, value.coerceIn(100, 600)).apply()

    var scrollSpeed: Float
        get() = prefs.getFloat(KEY_SCROLL_SPEED, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_SCROLL_SPEED, value.coerceIn(0.5f, 2.5f)).apply()

    var swapLeftRightClick: Boolean
        get() = prefs.getBoolean(KEY_SWAP_LEFT_RIGHT, false)
        set(value) = prefs.edit().putBoolean(KEY_SWAP_LEFT_RIGHT, value).apply()

    var invertScrollDirection: Boolean
        get() = prefs.getBoolean(KEY_INVERT_SCROLL, false)
        set(value) = prefs.edit().putBoolean(KEY_INVERT_SCROLL, value).apply()

    var middleClickAction: MiddleClickAction
        get() {
            val name = prefs.getString(KEY_MIDDLE_CLICK_ACTION, MiddleClickAction.SYSTEM_BACK.name)
            return try {
                MiddleClickAction.valueOf(name ?: MiddleClickAction.SYSTEM_BACK.name)
            } catch (e: Exception) {
                MiddleClickAction.SYSTEM_BACK
            }
        }
        set(value) = prefs.edit().putString(KEY_MIDDLE_CLICK_ACTION, value.name).apply()

    // Key mappings
    var keyUp: Int
        get() = prefs.getInt(KEY_MAPPING_UP, KeyEvent.KEYCODE_DPAD_UP)
        set(value) = prefs.edit().putInt(KEY_MAPPING_UP, value).apply()

    var keyDown: Int
        get() = prefs.getInt(KEY_MAPPING_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)
        set(value) = prefs.edit().putInt(KEY_MAPPING_DOWN, value).apply()

    var keyLeft: Int
        get() = prefs.getInt(KEY_MAPPING_LEFT, KeyEvent.KEYCODE_DPAD_LEFT)
        set(value) = prefs.edit().putInt(KEY_MAPPING_LEFT, value).apply()

    var keyRight: Int
        get() = prefs.getInt(KEY_MAPPING_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT)
        set(value) = prefs.edit().putInt(KEY_MAPPING_RIGHT, value).apply()

    var keyLeftClick: Int
        get() = prefs.getInt(KEY_MAPPING_CLICK, KeyEvent.KEYCODE_ENTER)
        set(value) = prefs.edit().putInt(KEY_MAPPING_CLICK, value).apply()

    var keyRightClick: Int
        get() = prefs.getInt(KEY_MAPPING_RIGHT_CLICK, KeyEvent.KEYCODE_BUTTON_R1)
        set(value) = prefs.edit().putInt(KEY_MAPPING_RIGHT_CLICK, value).apply()

    var keyMiddleClick: Int
        get() = prefs.getInt(KEY_MAPPING_MIDDLE_CLICK, KeyEvent.KEYCODE_M)
        set(value) = prefs.edit().putInt(KEY_MAPPING_MIDDLE_CLICK, value).apply()

    var keyDoubleClick: Int
        get() = prefs.getInt(KEY_MAPPING_DOUBLE_CLICK, KeyEvent.KEYCODE_UNKNOWN)
        set(value) = prefs.edit().putInt(KEY_MAPPING_DOUBLE_CLICK, value).apply()

    var keyDrag: Int
        get() = prefs.getInt(KEY_MAPPING_DRAG, KeyEvent.KEYCODE_SPACE)
        set(value) = prefs.edit().putInt(KEY_MAPPING_DRAG, value).apply()

    var keyScrollUp: Int
        get() = prefs.getInt(KEY_MAPPING_SCROLL_UP, KeyEvent.KEYCODE_PAGE_UP)
        set(value) = prefs.edit().putInt(KEY_MAPPING_SCROLL_UP, value).apply()

    var keyScrollDown: Int
        get() = prefs.getInt(KEY_MAPPING_SCROLL_DOWN, KeyEvent.KEYCODE_PAGE_DOWN)
        set(value) = prefs.edit().putInt(KEY_MAPPING_SCROLL_DOWN, value).apply()

    var keyFastMode: Int
        get() = prefs.getInt(KEY_MAPPING_FAST, KeyEvent.KEYCODE_SHIFT_LEFT)
        set(value) = prefs.edit().putInt(KEY_MAPPING_FAST, value).apply()

    var keyFineMode: Int
        get() = prefs.getInt(KEY_MAPPING_FINE, KeyEvent.KEYCODE_CTRL_LEFT)
        set(value) = prefs.edit().putInt(KEY_MAPPING_FINE, value).apply()

    var keyToggleCursor: Int
        get() = prefs.getInt(KEY_MAPPING_TOGGLE, KeyEvent.KEYCODE_C)
        set(value) = prefs.edit().putInt(KEY_MAPPING_TOGGLE, value).apply()

    // Floating Panel
    var isPanelEnabled: Boolean
        get() = prefs.getBoolean(KEY_PANEL_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_PANEL_ENABLED, value).apply()

    var isPanelCompact: Boolean
        get() = prefs.getBoolean(KEY_PANEL_COMPACT, false)
        set(value) = prefs.edit().putBoolean(KEY_PANEL_COMPACT, value).apply()

    var panelAlpha: Float
        get() = prefs.getFloat(KEY_PANEL_ALPHA, 0.92f)
        set(value) = prefs.edit().putFloat(KEY_PANEL_ALPHA, value.coerceIn(0.3f, 1.0f)).apply()

    var panelX: Int
        get() = prefs.getInt(KEY_PANEL_X, 40)
        set(value) = prefs.edit().putInt(KEY_PANEL_X, value).apply()

    var panelY: Int
        get() = prefs.getInt(KEY_PANEL_Y, 180)
        set(value) = prefs.edit().putInt(KEY_PANEL_Y, value).apply()

    // Automation
    var autoActivateOnConnect: Boolean
        get() = prefs.getBoolean(KEY_AUTO_ACTIVATE, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_ACTIVATE, value).apply()

    var autoDeactivateOnDisconnect: Boolean
        get() = prefs.getBoolean(KEY_AUTO_DEACTIVATE, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_DEACTIVATE, value).apply()

    var inactivityTimeoutSeconds: Int
        get() = prefs.getInt(KEY_INACTIVITY_TIMEOUT, 0)
        set(value) = prefs.edit().putInt(KEY_INACTIVITY_TIMEOUT, value).apply()

    var lastCursorX: Float
        get() = prefs.getFloat(KEY_LAST_X, 400f)
        set(value) = prefs.edit().putFloat(KEY_LAST_X, value).apply()

    var lastCursorY: Float
        get() = prefs.getFloat(KEY_LAST_Y, 600f)
        set(value) = prefs.edit().putFloat(KEY_LAST_Y, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
}
