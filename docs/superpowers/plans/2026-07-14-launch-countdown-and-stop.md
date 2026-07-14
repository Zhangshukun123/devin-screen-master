# Launch Countdown and Stop Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make device time authoritative, keep the preparation countdown active after its overlay closes, and add localized hold-to-stop guidance.

**Architecture:** Keep launch transitions in `DeviceLaunchStateController`. Let `DeviceLauncherActivity` own MQTT time updates and one preparation countdown handler, while the launcher XML owns the compact countdown and instruction placement. Store translations in the existing JSON language assets.

**Tech Stack:** Java, Android XML, ViewBinding, MQTT attributes, JUnit 4, Gradle Android plugin 7.4.2.

---

### Task 1: Add regression tests for launcher behavior

**Files:**
- Modify: `app/src/test/java/com/diwen/liliao/activity/DeviceStartFlowStructureTest.java`
- Modify: `app/src/test/java/com/diwen/liliao/layout/LauncherLayoutStructureTest.java`
- Create: `app/src/test/java/com/diwen/liliao/assets/StopHintTranslationsTest.java`

- [ ] **Step 1: Add source-level behavior assertions**

Add tests that reject the local treatment timer and require overlay-only back handling:

```java
@Test
public void treatmentTimeComesOnlyFromDeviceMessages() throws Exception {
    String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");
    assertFalse(source.contains("handler.sendEmptyMessage(826)"));
    assertFalse(source.contains("allTime--"));
    assertTrue(source.contains("PadSAttribute.DeviceTimeMin.getAttribute()"));
    assertTrue(source.contains("PadSAttribute.DeviceTimeSecond.getAttribute()"));
}

@Test
public void prepareBackOnlyHidesOverlay() throws Exception {
    String source = readSource("src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java");
    assertTrue(source.contains("hidePrepareOverlay()"));
    assertFalse(source.contains("returnToModeSelection()"));
}
```

- [ ] **Step 2: Add launcher layout assertions**

Replace the obsolete `modelName` assertion with the current bottom-bar contract, then require the compact countdown and hint:

```java
assertTrue(containsId(llBottom, "@+id/modelName"));
assertEquals("@id/tvSeconds", compactCountdown.getAttribute("app:layout_constraintStart_toEndOf"));
assertEquals("@mipmap/ic_prepare_hourglass", hourglass.getAttribute("android:src"));
assertEquals("@id/rl_start", stopHint.getAttribute("app:layout_constraintTop_toBottomOf"));
```

- [ ] **Step 3: Add exact translation assertions**

Create a parameterized-style loop using FastJSON:

```java
Map<String, String> expected = new LinkedHashMap<>();
expected.put("zh.json", "长按3秒停止");
expected.put("en.json", "HOLD 3S TO STOP");
expected.put("fr.json", "MAINTENIR 3S POUR ARRÊTER");
expected.put("de.json", "3S DRÜCKEN ZUM STOPPEN");
expected.put("it.json", "TENERE 3S PER FERMARE");
expected.put("es.json", "MANTENER 3S PARA DETENER");
for (Map.Entry<String, String> entry : expected.entrySet()) {
    String json = new String(Files.readAllBytes(
            new File("src/main/assets", entry.getKey()).toPath()), StandardCharsets.UTF_8);
    assertEquals(entry.getValue(), JSON.parseObject(json).getString("长按3秒停止"));
}
```

- [ ] **Step 4: Run the new tests and confirm RED**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.diwen.liliao.activity.DeviceStartFlowStructureTest" --tests "com.diwen.liliao.layout.LauncherLayoutStructureTest" --tests "com.diwen.liliao.assets.StopHintTranslationsTest" --no-daemon
```

Expected: failures for handler `826`, missing `hidePrepareOverlay`, missing compact countdown/hint elements, and missing translation keys.

### Task 2: Make device time authoritative and separate overlay visibility from countdown cancellation

**Files:**
- Modify: `app/src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java`
- Preserve existing change: `app/src/main/java/com/diwen/liliao/activity/DeviceLaunchStateController.java`

- [ ] **Step 1: Remove the local treatment-time loop**

Delete message `826`, `allTime`, `setTime()`, the launch-state scheduling block, and related message cleanup. Keep MQTT values as the only writers:

```java
if (strings.contains(PadSAttribute.DeviceTimeMin.getAttribute())) {
    mine = (int) map.get(PadSAttribute.DeviceTimeMin.getAttribute());
    binding.tvMine.setText(getPointTwo(mine));
}
if (strings.contains(PadSAttribute.DeviceTimeSecond.getAttribute())) {
    seconds = (int) map.get(PadSAttribute.DeviceTimeSecond.getAttribute());
    binding.tvSeconds.setText(getPointTwo(seconds));
}
```

- [ ] **Step 2: Synchronize both preparation countdown views**

Add one update method and one overlay-only method:

```java
private void updatePrepareCountdownUi(int remainingSeconds) {
    String value = String.valueOf(remainingSeconds);
    binding.tvPrepareSeconds.setText(value);
    binding.tvPrepareMainSeconds.setText(value);
    binding.prepareCompactCountdown.setVisibility(View.VISIBLE);
}

private void hidePrepareOverlay() {
    binding.prepareOverlay.setVisibility(View.GONE);
}
```

Update `startPrepareCountdown()` and message `827` to call `updatePrepareCountdownUi`. Update `cancelPrepareCountdown()` to remove message `827`, hide the overlay, and hide `prepareCompactCountdown`.

- [ ] **Step 3: Keep preparation active when the overlay closes**

Use `hidePrepareOverlay()` for both back paths:

```java
if (v == binding.ivPrepareBack) {
    hidePrepareOverlay();
}

@Override
public void onBackPressed() {
    if (binding.prepareOverlay.getVisibility() == View.VISIBLE) {
        hidePrepareOverlay();
        return;
    }
    super.onBackPressed();
}
```

Remove `returnToModeSelection()` because the preparation overlay no longer changes activities.

- [ ] **Step 4: Wait for device preparation confirmation and enforce a three-second hold**

Do not start the countdown from the short-click handler. Start it only after MQTT updates `Launch` to the preparation state:

```java
if (Launch == DeviceLaunchStateController.LAUNCH_PREPARING
        && !handler.hasMessages(827)) {
    startPrepareCountdown();
}
```

Keep short press returning `NO_LAUNCH_COMMAND` for preparation. Replace Android's default long-click callback with touch handling that posts the STOP command after `3000L`, cancels it on early release or cancellation, and invalidates both gestures if the pointer leaves the control bounds. Preserve the existing controller change that returns `LAUNCH_STOPPED` for every completed hold.

- [ ] **Step 5: Run behavior tests and confirm GREEN**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.diwen.liliao.activity.DeviceStartFlowStructureTest" --no-daemon
```

Expected: all `DeviceStartFlowStructureTest` tests pass.

### Task 3: Add compact countdown, icon, hint, and translations

**Files:**
- Modify: `app/src/main/res/layout/layout_devicelauncheractivity.xml`
- Create: `app/src/main/res/mipmap-xxhdpi/ic_prepare_hourglass.png`
- Modify: `app/src/main/assets/zh.json`
- Modify: `app/src/main/assets/en.json`
- Modify: `app/src/main/assets/fr.json`
- Modify: `app/src/main/assets/de.json`
- Modify: `app/src/main/assets/it.json`
- Modify: `app/src/main/assets/es.json`
- Modify: `app/src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java`

- [ ] **Step 1: Add the compact countdown to the layout**

Place this group after `tvSeconds`:

```xml
<LinearLayout
    android:id="@+id/prepareCompactCountdown"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginStart="14dp"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:visibility="gone"
    app:layout_constraintBottom_toBottomOf="@id/tvSeconds"
    app:layout_constraintStart_toEndOf="@id/tvSeconds">

    <TextView
        android:id="@+id/tvPrepareMainSeconds"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="10"
        android:textColor="@color/white"
        android:textSize="34sp"
        android:textStyle="bold" />

    <ImageView
        android:id="@+id/ivPrepareHourglass"
        android:layout_width="13dp"
        android:layout_height="13dp"
        android:layout_marginStart="4dp"
        android:src="@mipmap/ic_prepare_hourglass" />
</LinearLayout>
```

- [ ] **Step 2: Add the stop hint below the main control**

Change `rl_start` to constrain above `tvHoldToStopHint`, then add:

```xml
<TextView
    android:id="@+id/tvHoldToStopHint"
    android:layout_width="220dp"
    android:layout_height="wrap_content"
    android:gravity="center"
    android:maxLines="1"
    android:text="HOLD 3S TO STOP"
    android:textColor="@color/white"
    android:textSize="8sp"
    app:layout_constraintBottom_toTopOf="@id/ll_bottom"
    app:layout_constraintEnd_toEndOf="@id/rl_start"
    app:layout_constraintStart_toStartOf="@id/rl_start"
    app:layout_constraintTop_toBottomOf="@id/rl_start" />
```

- [ ] **Step 3: Copy the supplied icon and add language keys**

Copy the supplied 25 by 25 PNG to `ic_prepare_hourglass.png`. Add `"长按3秒停止"` with the exact workbook values to all six language files.

- [ ] **Step 4: Bind the localized hint**

Add to `setUiText()`:

```java
binding.tvHoldToStopHint.setText(StringUtils.getUpperText("长按3秒停止"));
```

- [ ] **Step 5: Run layout and translation tests and confirm GREEN**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.diwen.liliao.layout.LauncherLayoutStructureTest" --tests "com.diwen.liliao.assets.StopHintTranslationsTest" --no-daemon
```

Expected: both test classes pass.

### Task 4: Verify, package, review, commit, and push

**Files:**
- Verify all modified source, resource, asset, test, and documentation files.

- [ ] **Step 1: Run the complete unit-test task**

Run:

```powershell
.\gradlew.bat testDebugUnitTest --no-daemon
```

Expected: 0 failed tests.

- [ ] **Step 2: Build the debug APK**

Run:

```powershell
.\gradlew.bat assembleDebug --no-daemon
```

Expected: `BUILD SUCCESSFUL` and `build/app/outputs/apk/debug/Magique_debug_v1.0_20260714.apk` exists.

- [ ] **Step 3: Inspect the final diff and APK metadata**

Run:

```powershell
git diff --check
git status --short
Get-FileHash -Algorithm SHA256 -LiteralPath 'build/app/outputs/apk/debug/Magique_debug_v1.0_20260714.apk'
```

Expected: no whitespace errors; the status lists only intended files; SHA-256 is printed.

- [ ] **Step 4: Request code review and resolve findings**

Review the diff against `docs/superpowers/specs/2026-07-14-launch-countdown-and-stop-design.md`. Fix critical and important findings, then rerun tests and the debug build.

- [ ] **Step 5: Commit all intended changes**

Run:

```powershell
git add -- app/src/main/java/com/diwen/liliao/activity/DeviceLaunchStateController.java app/src/main/java/com/diwen/liliao/activity/DeviceLauncherActivity.java app/src/main/res/layout/layout_devicelauncheractivity.xml app/src/main/res/mipmap-xxhdpi/ic_prepare_hourglass.png app/src/main/assets/zh.json app/src/main/assets/en.json app/src/main/assets/fr.json app/src/main/assets/de.json app/src/main/assets/it.json app/src/main/assets/es.json app/src/test/java/com/diwen/liliao/activity/DeviceStartFlowStructureTest.java app/src/test/java/com/diwen/liliao/layout/LauncherLayoutStructureTest.java app/src/test/java/com/diwen/liliao/assets/StopHintTranslationsTest.java docs/superpowers/plans/2026-07-14-launch-countdown-and-stop.md
git commit -m "fix: synchronize launcher countdown and stop controls"
```

- [ ] **Step 6: Push the current branch**

Run:

```powershell
git push origin develop
```

Expected: `develop` updates on the GitHub remote.
