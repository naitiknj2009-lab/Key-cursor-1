# KeyCursor – Hardware Keyboard Mouse Controller for Android

KeyCursor is a native Android accessibility application designed specifically for **Android 10+** tablets and devices (including the **Lenovo Tab M10**). It converts any physical **USB** or **Bluetooth** keyboard into a smooth, system-wide mouse controller with custom pointer styles, acceleration curves, touch dispatch gestures, and a floating on-screen control HUD.

---

## 🌟 Key Features

### 1. Advanced Cursor Movement Engine
- **Arrow Key Motion**: Move cursor in 8 directions using the physical keyboard arrow keys (`Up`, `Down`, `Left`, `Right`).
- **Diagonal Normalization**: Hold two arrow keys simultaneously (e.g. `Up` + `Right`) with constant normalized vector speed ($v / \sqrt{2}$).
- **Continuous Hold & Acceleration**: Smooth 60 FPS physics ticker that increases velocity gradually while holding keys.
- **Speed Presets**: Instant switching between **Slow (0.5x)**, **Normal (1.0x)**, and **Fast (2.0x)** presets.
- **Modifier Speeds**:
  - `Shift + Arrow`: Fast movement mode (2.4x speed multiplier).
  - `Ctrl + Arrow`: Fine precision mode (0.35x speed multiplier).
- **Display Boundary Clamping**: Prevents cursor from ever moving outside the screen edges; adapts seamlessly to portrait, landscape, and multi-window resizing.
- **Persistent Coordinates**: Remembers the exact last cursor position across app transitions and restarts.
- **Zero CPU Idle Footprint**: The movement ticker loop only runs while directional keys are physically pressed; zero battery drain when idle.

### 2. Complete Real Touch & Mouse Operations
- **Left Click**: Press `Enter` (or Numpad Enter) to perform a real touch tap at current cursor coordinates.
- **Right Click Equivalent**: Press `Shift + Enter` to dispatch a contextual long-press (450ms) gesture, triggering Android context menus, copy/paste bars, and popup options.
- **Middle Click Action**: Configurable system shortcut mapped to `M` (or any custom key), executing:
  - System Back button
  - System Home button
  - Open Notifications shade
  - Recent Apps overview
  - Double-tap at cursor position
- **Swap Left & Right Buttons**: Switch primary and secondary click behaviors.
- **Natural / Inverted Scrolling**: Toggle scroll direction inversion to match desktop trackpad preferences.
- **Double Click**: Double tap `Enter` or press a dedicated key to dispatch rapid consecutive touches with configurable interval timing.
- **Continuous Drag & Drop**: Hold `Space` (or tap to toggle) and navigate arrow keys to drag items, folders, or sliders; release `Space` to drop.
- **Long Press**: Dedicated long-touch dispatch for drag handles and app launcher icons.
- **Scrolling**:
  - `Page Up` / `Page Down`: Smooth vertical content scrolling.
  - `Shift + Page Up` / `Page Down`: Horizontal scrolling.
- **Tactile Haptic Feedback**: Subtle vibration on clicks and drag actions.

### 3. Interactive Key Customizer & Remapping
- **Live Physical Key Capture**: Tap any action and press any key on your physical keyboard to immediately bind it.
- **Key Catalog**: Visual picker supporting Letters (A-Z), Numbers (0-9), Arrow keys, Function keys (F1-F12), Numpad, and Modifiers.
- **Remap Any Action**: Move Up/Down/Left/Right, Left Click, Right Click, Middle Click, Double Click, Drag & Drop, Fast Speed, Fine Speed, Scroll Up/Down, and Typing Mode Toggle.

### 4. Framer Motion-Inspired Physics & Animations
- **Spring-Based Press Physics (`whileTap={{ scale: 0.96 }}`)**: Cards, buttons, and switches feature bouncy spring physics (`MotionSprings.Bouncy`) with snappy release responses.
- **Harmonic Ambient Glows**: Smooth pulsing cyan illumination on active master controls.
- **Fluid Layout Resizing**: Dynamic height transitions (`animateContentSize`) when toggling settings and options.

### 3. Cursor Customization
- **4 Pointer Styles**:
  - `Cyber Glow Arrow`: Faceted neon pointer with luminous accent.
  - `Classic Mouse`: Traditional desktop OS pointer with border and shadow.
  - `Precision Reticle`: Circular targeting reticle with center alignment dot.
  - `Target Crosshair`: 4-axis crosshair for pixel-precise targeting.
- **Custom Sizing**: 16dp to 64dp adjustable scale.
- **Color Presets**: Neon Cyan, Matrix Emerald, Sunset Amber, Cyber Rose, Purple Glow, Electric Blue, and Pure White.
- **Visual Effects**: Hardware-accelerated drop shadows, click ripple waves, and pulsing drag aura rings.
- **Live Interactive Preview**: Test click ripples and drag aura directly in the settings dashboard before applying.

### 4. Floating Control Panel (HUD)
- **Overlay Controls**: Movable floating widget that stays visible across all Android applications.
- **Quick Toggles**: Cursor ON/OFF, Mouse Mode vs Typing Mode, Speed Preset cycler, Visibility eye toggle, and Dashboard shortcut.
- **Compact Pill Mode**: Collapses into a minimal pill to prevent obstructing screen content; tap to expand.
- **Free Movement**: Drag anywhere on screen with finger touch; saves position automatically.

### 5. Smart Keyboard Detection & Typing Mode
- **Automatic Device Monitoring**: Detects physical USB keyboards (via OTG) and Bluetooth keyboards using Android `InputManager`.
- **Typing Mode (`Alt + C`)**: Prevents keyboard mouse controls from intercepting normal typing. Press `Alt + C` or tap the mode button to type freely in documents, search bars, and terminals; press `Alt + C` again to return to mouse control.
- **Automation**: Optional auto-activation when a keyboard connects and auto-deactivation on disconnect.

### 6. Persistent Notification Controls
- Foreground service notification channel with quick actions: **Pause/Resume**, **Speed Preset Cycle**, **Hide/Show Cursor**, and **Open App**.

---

## 🚀 Installation & Permissions Setup

### Prerequisites
- **Target Device**: Lenovo Tab M10 or any Android tablet/phone running **Android 10 (API 29)** or newer.
- **Input Hardware**: Any standard USB keyboard (connected via USB-C OTG adapter) or Bluetooth keyboard.

### Step-by-Step Setup
1. **Install the APK**:
   ```bash
   adb install app-debug.apk
   ```
2. **Launch KeyCursor**: Open KeyCursor from the app launcher.
3. **Enable Accessibility Service**:
   - Tap **"Enable KeyCursor in Settings"** on the dashboard.
   - In Android Settings, navigate to **Accessibility** > **Downloaded Services** (or **Installed Services**).
   - Select **KeyCursor** and toggle the switch to **ON**.
   - Tap **Allow** on the permission prompt.
4. **Connect Keyboard**: Plug in your USB keyboard or pair via Bluetooth. KeyCursor will show a green "Connected" badge with the device model name.
5. **Start Controlling**: Use the arrow keys and `Enter` to control your tablet like a PC!

---

## ⌨️ Complete Default Keyboard Shortcuts

| Shortcut | Action | Description |
|---|---|---|
| `Up` / `Down` / `Left` / `Right` | Move Cursor | Moves cursor smoothly with hold acceleration |
| `Up + Right`, etc. | Diagonal Move | Moves diagonally at normalized speed |
| `Shift + Arrow` | Fast Speed | 2.4x speed burst |
| `Ctrl + Arrow` | Fine Precision | 0.35x pixel-level precision |
| `Enter` | Left Click | Dispatches a single touch tap at cursor tip |
| `Double Enter` | Double Click | Dispatches two consecutive taps |
| `Shift + Enter` | Right Click | Context menu / long-press equivalent |
| `Space` (Hold) | Drag & Drop | Drag while moving arrows; release to drop |
| `Page Up` / `Page Down` | Vertical Scroll | Scrolls content up / down |
| `Shift + Page Up/Down` | Horizontal Scroll | Scrolls content left / right |
| `Alt + C` | Toggle Typing Mode | Switches between Mouse Control and Normal Typing |

---

## 🛡️ Privacy, Security & Platform Limitations

- **100% Offline & Private**: KeyCursor contains zero tracking, zero telemetry, no internet permission, and does not record or transmit any keystrokes.
- **Secure Fields (FLAG_SECURE)**: For user protection, the Android operating system prevents Accessibility Services from dispatching synthetic touch gestures into sensitive system password inputs and secure banking screens.
- **Right Click Translation**: Android OS touch architecture does not feature a native secondary mouse click event in `AccessibilityService.dispatchGesture`. KeyCursor implements the universal Android alternative: contextual long-press (450ms), which triggers context menus, selection bars, and menu popups across all apps.

---

## 🛠️ Manual APK Compilation

This project is built using modern Gradle with Kotlin DSL and Jetpack Compose.

### Build Debug APK:
```bash
gradle :app:assembleDebug
```
The resulting APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Run Local Unit & Robolectric Tests:
```bash
gradle :app:testDebugUnitTest
```

---

## 📱 Hardware Optimization Note (Lenovo Tab M10 / 3 GB RAM)
- Compiled with hardware layer acceleration for `CursorView`.
- Memory footprint is kept strictly under **35 MB**.
- All ticker coroutines suspend immediately upon key release to prevent CPU wake locks and preserve tablet battery life.
