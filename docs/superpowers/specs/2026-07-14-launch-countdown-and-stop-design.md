# Launch Countdown and Stop Interaction Design

## Goal

Update the treatment launcher so device-reported treatment time is authoritative, the preparation countdown survives closing its full-screen overlay, and users can stop the device by holding the main control for three seconds.

## Scope

The change covers `DeviceLauncherActivity`, its launcher layout, the launch-state controller, localized JSON assets, the supplied hourglass icon, and related unit/structure tests. It preserves the current screen architecture and MQTT protocol.

## Behavior

### Treatment time

`DeviceTimeMin` and `DeviceTimeSecond` from the device update `tvMine` and `tvSeconds`. The app does not decrement treatment time locally. This avoids drift because the device publishes the current values once per second while running.

### Preparation countdown

Starting from `Launch=2` sends `Launch=3` and starts the existing preparation countdown. The full-screen preparation overlay displays the countdown first.

Pressing either the overlay back button or the Android back button hides only the overlay. The countdown handler continues running, and a compact countdown beside the main treatment time shows the same remaining value. The compact view contains the supplied hourglass icon.

When the countdown reaches zero, the app hides both countdown views and sends `Launch=1`. If the device leaves `Launch=3` before completion, the app cancels the countdown and hides both views.

### Main control

A short press follows the existing state controller. It does nothing during `Launch=3`.

A long press sends `Launch=2` from every state. This matches the permanent instruction below the control and includes the required preparation-state stop behavior.

### Stop instruction

The launcher shows one localized line below `rlStart`. The translations are:

| Language | Text |
| --- | --- |
| Chinese | 长按3秒停止 |
| English | HOLD 3S TO STOP |
| French | MAINTENIR 3S POUR ARRÊTER |
| German | 3S DRÜCKEN ZUM STOPPEN |
| Italian | TENERE 3S PER FERMARE |
| Spanish | MANTENER 3S PARA DETENER |

The app stores these values under one new Chinese lookup key in the six language JSON files and resolves the visible text through the existing language service.

## Code structure

`DeviceLaunchStateController` remains the source of launch-button transitions. `DeviceLauncherActivity` owns MQTT updates, countdown lifecycle, and view visibility. The XML layout owns placement of the compact countdown and stop instruction. No new activity, fragment, or protocol attribute is required.

The implementation separates two countdown actions:

- Hide the full-screen overlay while keeping the countdown active.
- Cancel the countdown when preparation ends or the launch state changes.

This separation fixes the current back-navigation bug without creating another timer.

## Testing

Tests will verify:

- Short press returns no command for `Launch=3` and long press returns `Launch=2`.
- Launcher source no longer schedules or decrements the treatment-time handler.
- Overlay back actions hide the overlay without opening the mode-selection screen or finishing the launcher.
- The layout contains the compact countdown, hourglass resource, and stop instruction below the main control.
- Every supported language contains the exact stop instruction from the supplied workbook.
- The full unit-test task and debug APK build complete successfully.

The existing launcher layout test contains an obsolete assertion about `modelName`. Since this task changes the same layout test suite, the implementation will update that assertion to match the current visible bottom-bar design before adding new layout checks.

## Delivery

The final commit will include source changes, tests, language assets, and the supplied icon. The debug APK will use the repository's existing `Magique_debug_v1.0_YYYYMMDD.apk` naming rule. The completed commit will be pushed to `origin/develop`.
