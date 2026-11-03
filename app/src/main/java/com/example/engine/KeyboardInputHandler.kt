package com.example.engine

import android.view.KeyEvent
import com.example.data.CursorStateHolder
import com.example.data.DragTriggerMode
import com.example.data.KeyCursorPreferences

/**
 * Intercepts physical keyboard inputs and translates them into cursor motion and mouse gestures.
 * Handles key down / up events, diagonal combinations, modifier speed shifts,
 * and seamless switching between Cursor Mode and Typing Mode.
 */
class KeyboardInputHandler(
    private val prefs: KeyCursorPreferences,
    private val cursorEngine: CursorEngine,
    private val gestureEngine: GestureEngine,
    private val onModeToggle: ((isTypingMode: Boolean) -> Unit)? = null
) {
    // Current direction tracking
    private var isUpHeld = false
    private var isDownHeld = false
    private var isLeftHeld = false
    private var isRightHeld = false

    // Modifiers tracking
    private var isShiftHeld = false
    private var isCtrlHeld = false
    private var isAltHeld = false

    // Double-click timing
    private var lastEnterPressTimestamp = 0L

    // Typing Mode toggle state: when true, all keys pass through cleanly to system
    var isTypingMode: Boolean = false
        private set

    fun setTypingModeState(typingMode: Boolean) {
        isTypingMode = typingMode
        CursorStateHolder.setTypingMode(typingMode)
        if (typingMode) {
            resetDirectionalKeys()
            if (gestureEngine.isDraggingActive()) {
                gestureEngine.endDrag(cursorEngine.currentX, cursorEngine.currentY)
            }
        }
        onModeToggle?.invoke(typingMode)
    }

    fun toggleTypingMode(): Boolean {
        setTypingModeState(!isTypingMode)
        return isTypingMode
    }

    /**
     * Intercepts and processes a KeyEvent.
     * Returns true if event is consumed, false to pass through.
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        // Track modifiers across all keys
        isShiftHeld = event.isShiftPressed
        isCtrlHeld = event.isCtrlPressed
        isAltHeld = event.isAltPressed

        cursorEngine.setModifiers(isShiftHeld, isCtrlHeld)

        val keyCode = event.keyCode
        val action = event.action
        val isDown = action == KeyEvent.ACTION_DOWN
        val isUp = action == KeyEvent.ACTION_UP

        // 1. Check for Cursor/Typing Mode toggle shortcut (Alt + C or custom mapped toggle)
        if (isDown && ((isAltHeld && keyCode == KeyEvent.KEYCODE_C) || keyCode == prefs.keyToggleCursor)) {
            toggleTypingMode()
            return true
        }

        // If Cursor is disabled in preferences or in Typing Mode, let all keys pass through!
        if (!prefs.isCursorEnabled || isTypingMode) {
            return false
        }

        // 2. Directional Movement (Arrow keys or custom mapped)
        val isUpKey = keyCode == prefs.keyUp || keyCode == KeyEvent.KEYCODE_DPAD_UP
        val isDownKey = keyCode == prefs.keyDown || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
        val isLeftKey = keyCode == prefs.keyLeft || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
        val isRightKey = keyCode == prefs.keyRight || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT

        if (isUpKey || isDownKey || isLeftKey || isRightKey) {
            if (isDown) {
                if (isUpKey) isUpHeld = true
                if (isDownKey) isDownHeld = true
                if (isLeftKey) isLeftHeld = true
                if (isRightKey) isRightHeld = true
            } else if (isUp) {
                if (isUpKey) isUpHeld = false
                if (isDownKey) isDownHeld = false
                if (isLeftKey) isLeftHeld = false
                if (isRightKey) isRightHeld = false
            }

            cursorEngine.setDirectionPressed(isUpHeld, isDownHeld, isLeftHeld, isRightHeld)
            return true
        }

        // 3. Mouse Click Actions (Enter / Shift+Enter / Custom)
        val isClickKey = keyCode == prefs.keyLeftClick || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER
        val isDedicatedRightClick = keyCode == prefs.keyRightClick
        val isDedicatedMiddleClick = keyCode == prefs.keyMiddleClick
        val isDedicatedDoubleClick = keyCode == prefs.keyDoubleClick && prefs.keyDoubleClick != KeyEvent.KEYCODE_UNKNOWN

        if (isDedicatedMiddleClick && isDown && event.repeatCount == 0) {
            gestureEngine.performMiddleClick(cursorEngine.currentX, cursorEngine.currentY)
            return true
        }

        if (isDedicatedDoubleClick && isDown && event.repeatCount == 0) {
            gestureEngine.performDoubleClick(cursorEngine.currentX, cursorEngine.currentY)
            return true
        }

        if (isClickKey || isDedicatedRightClick) {
            if (isDown && event.repeatCount == 0) {
                val now = System.currentTimeMillis()
                var isRightClick = isShiftHeld || isDedicatedRightClick

                // Swap buttons if configured by user
                if (prefs.swapLeftRightClick) {
                    isRightClick = !isRightClick
                }

                if (isRightClick) {
                    // Right Click equivalent (Context Menu)
                    gestureEngine.performRightClick(cursorEngine.currentX, cursorEngine.currentY)
                } else if (prefs.doubleClickEnabled && (now - lastEnterPressTimestamp) < prefs.doubleClickIntervalMs) {
                    // Double Click
                    gestureEngine.performDoubleClick(cursorEngine.currentX, cursorEngine.currentY)
                    lastEnterPressTimestamp = 0L
                } else {
                    // Left Click
                    lastEnterPressTimestamp = now
                    gestureEngine.performLeftClick(cursorEngine.currentX, cursorEngine.currentY)
                }
            }
            return true
        }

        // 4. Drag & Drop (Space key)
        val isDragKey = keyCode == prefs.keyDrag || keyCode == KeyEvent.KEYCODE_SPACE
        if (isDragKey) {
            if (prefs.dragTriggerMode == DragTriggerMode.HOLD_SPACE) {
                if (isDown && event.repeatCount == 0) {
                    gestureEngine.startDrag(cursorEngine.currentX, cursorEngine.currentY)
                } else if (isUp) {
                    gestureEngine.endDrag(cursorEngine.currentX, cursorEngine.currentY)
                }
            } else {
                // TOGGLE_SPACE
                if (isDown && event.repeatCount == 0) {
                    if (gestureEngine.isDraggingActive()) {
                        gestureEngine.endDrag(cursorEngine.currentX, cursorEngine.currentY)
                    } else {
                        gestureEngine.startDrag(cursorEngine.currentX, cursorEngine.currentY)
                    }
                }
            }
            return true
        }

        // 5. Scrolling (Page Up / Page Down)
        val isScrollUpKey = keyCode == prefs.keyScrollUp || keyCode == KeyEvent.KEYCODE_PAGE_UP
        val isScrollDownKey = keyCode == prefs.keyScrollDown || keyCode == KeyEvent.KEYCODE_PAGE_DOWN

        if (isScrollUpKey && isDown) {
            if (isShiftHeld) {
                gestureEngine.performHorizontalScroll(cursorEngine.currentX, cursorEngine.currentY, left = true)
            } else {
                gestureEngine.performVerticalScroll(cursorEngine.currentX, cursorEngine.currentY, up = true)
            }
            return true
        }

        if (isScrollDownKey && isDown) {
            if (isShiftHeld) {
                gestureEngine.performHorizontalScroll(cursorEngine.currentX, cursorEngine.currentY, left = false)
            } else {
                gestureEngine.performVerticalScroll(cursorEngine.currentX, cursorEngine.currentY, up = false)
            }
            return true
        }

        // If not a cursor key, let it pass through to the system
        return false
    }

    fun resetDirectionalKeys() {
        isUpHeld = false
        isDownHeld = false
        isLeftHeld = false
        isRightHeld = false
        cursorEngine.stopMovement()
    }
}
