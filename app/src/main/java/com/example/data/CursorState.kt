package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CursorUiState(
    val isServiceRunning: Boolean = false,
    val isCursorActive: Boolean = true,
    val isCursorVisible: Boolean = true,
    val isTypingMode: Boolean = false,
    val x: Float = 400f,
    val y: Float = 600f,
    val isClicking: Boolean = false,
    val isDragging: Boolean = false,
    val isFastMode: Boolean = false,
    val isFineMode: Boolean = false,
    val speedPreset: SpeedPreset = SpeedPreset.NORMAL,
    val connectedKeyboardsCount: Int = 0,
    val connectedKeyboardNames: List<String> = emptyList(),
    val hasPhysicalKeyboard: Boolean = false,
    val lastActionDescription: String = "Ready",
    val hasOverlayPermission: Boolean = false,
    val screenWidth: Int = 1200,
    val screenHeight: Int = 1920
)

object CursorStateHolder {
    private val _state = MutableStateFlow(CursorUiState())
    val state: StateFlow<CursorUiState> = _state.asStateFlow()

    fun update(transform: (CursorUiState) -> CursorUiState) {
        _state.value = transform(_state.value)
    }

    fun setServiceRunning(running: Boolean) {
        update { it.copy(isServiceRunning = running) }
    }

    fun setCursorActive(active: Boolean) {
        update { it.copy(isCursorActive = active) }
    }

    fun setTypingMode(typing: Boolean) {
        update { it.copy(isTypingMode = typing) }
    }

    fun setCursorVisible(visible: Boolean) {
        update { it.copy(isCursorVisible = visible) }
    }

    fun setPosition(x: Float, y: Float) {
        update { it.copy(x = x, y = y) }
    }

    fun setClicking(clicking: Boolean) {
        update { it.copy(isClicking = clicking) }
    }

    fun setDragging(dragging: Boolean) {
        update { it.copy(isDragging = dragging) }
    }

    fun setFastMode(fast: Boolean) {
        update { it.copy(isFastMode = fast) }
    }

    fun setFineMode(fine: Boolean) {
        update { it.copy(isFineMode = fine) }
    }

    fun setSpeedPreset(preset: SpeedPreset) {
        update { it.copy(speedPreset = preset) }
    }

    fun setKeyboards(names: List<String>) {
        update {
            it.copy(
                connectedKeyboardsCount = names.size,
                connectedKeyboardNames = names,
                hasPhysicalKeyboard = names.isNotEmpty()
            )
        }
    }

    fun setAction(action: String) {
        update { it.copy(lastActionDescription = action) }
    }

    fun setOverlayPermission(hasPermission: Boolean) {
        update { it.copy(hasOverlayPermission = hasPermission) }
    }

    fun setScreenSize(width: Int, height: Int) {
        update { it.copy(screenWidth = width, screenHeight = height) }
    }
}
