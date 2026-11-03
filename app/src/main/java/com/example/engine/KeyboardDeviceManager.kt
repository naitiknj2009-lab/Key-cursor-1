package com.example.engine

import android.content.Context
import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import com.example.data.CursorStateHolder

class KeyboardDeviceManager(
    private val context: Context,
    private val onKeyboardConnectionChanged: ((connected: Boolean, deviceName: String) -> Unit)? = null
) {
    private val inputManager: InputManager? =
        context.getSystemService(Context.INPUT_SERVICE) as? InputManager

    private val mainHandler = Handler(Looper.getMainLooper())

    private val inputDeviceListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            checkKeyboards()
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            checkKeyboards()
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            checkKeyboards()
        }
    }

    fun startListening() {
        inputManager?.registerInputDeviceListener(inputDeviceListener, mainHandler)
        checkKeyboards()
    }

    fun stopListening() {
        inputManager?.unregisterInputDeviceListener(inputDeviceListener)
    }

    fun checkKeyboards(): List<String> {
        val names = mutableListOf<String>()
        val deviceIds = InputDevice.getDeviceIds()
        for (id in deviceIds) {
            val device = InputDevice.getDevice(id) ?: continue
            if (isPhysicalKeyboard(device)) {
                val isBluetooth = device.name.contains("Bluetooth", ignoreCase = true) ||
                        device.name.contains("BT", ignoreCase = true) ||
                        (device.vendorId == 0 && device.isExternal)
                val type = if (isBluetooth) "Bluetooth" else "USB / External"
                names.add("${device.name} ($type)")
            }
        }

        CursorStateHolder.setKeyboards(names)
        onKeyboardConnectionChanged?.invoke(names.isNotEmpty(), names.firstOrNull() ?: "")
        return names
    }

    private fun isPhysicalKeyboard(device: InputDevice): Boolean {
        if (device.isVirtual) return false
        val sources = device.sources
        val isKeyboardSource = (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD
        val hasAlphabet = device.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC
        return isKeyboardSource && hasAlphabet
    }
}
