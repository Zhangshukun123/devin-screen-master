# 项目记忆

## 项目概况
- Android 项目（com.diwen.liliao），设备控制类 App，使用 MQTT、EventBus、ViewBinding。
- Fragment 基类：`BaseFragment<T extends ViewBinding>`，根视图通过 `binding.getRoot()` 获取。

## 修改记录
- PemfFragment（频率/治疗时间输入框）：添加点击空白处隐藏输入法逻辑。
  - 在 `initData()` 末尾调用 `setupHideKeyboardOnTouch()`，为根视图设置 OnTouchListener，
    ACTION_DOWN 时若触摸点在当前获焦的 EditText 区域外，则隐藏键盘并清除焦点。
  - 触摸事件返回 false，不消费，避免影响其他控件点击。

## PEMF 工作状态 (PemfWorkState)
- 新增属性 `PadSAttribute.PemfWorkState`（"PemfWorkState"），0：停止，1：启动。
- 启动/停止按钮（btnPemfManual）改为通过 PemfWorkState 判断与控制：
  - 点击手动按钮时发送 PemfWorkState（1=启动，0=停止）。
  - 接收到设备 PemfWorkState 时更新 pemfRunning 与 UI。
- queryPemfAttribute（BTCodeUtils）查询时也带上 PemfWorkState。
- 注意：PemfState 仍表示 自动/手动 模式（1=自动），PemfWorkState 表示手动模式下的运行状态。

## PEMF 功能开关 (PemfEnable)
- 新增属性 `PadSAttribute.PemfEnable`（"PemfEnable"），0：无 PEMF 功能，1：有。
- BTCodeUtils 新增 `queryPemfEnable()`，下发 PEMF 询问指令。
- DeviceSettingActivity（PEMF 入口所在页）：
  - `config()` 连接后调用 `queryPemfEnable()` 查询；先读取 MMKV 缓存值作为初始 UI。
  - 添加 @BindEventBus + onEventMainThread + MqttMessage，监听 PemfEnable 返回与 onlinestate=1 时重新查询。
  - `applyPemfEnableUi()`：无功能时将 llPemf 置灰（alpha 0.4）并 setEnabled(false)；点击 llPemf 时若 !pemfEnable 直接 return，不跳转 PEMF 页面。
  - PemfEnable 缓存于 MyMMKV.PemfEnable（boolean）。

## 构建环境注意
- 本机 gradlew 编译因 Lombok 与当前 JDK 模块系统不兼容报错
  （LombokProcessor cannot access JavacProcessingEnvironment），属环境问题，非代码问题。
- 运行 gradlew 需用 `.\gradlew.bat`（PowerShell 不从当前目录加载命令）。

## 主界面底部中间显示当前模式 (modelName)
- 问题：layout_devicelauncheractivity.xml 中 `@+id/modelName` TextView 被设为 0dp 且 visibility=gone，导致底部中间不显示当前模式。
- 代码侧 DeviceLauncherActivity 已在 setUiText() 和 MqttMessage(PluseMode) 时给 binding.modelName setText 模式名（按 PluseMode 匹配 settingItems 的 title）。
- 修复：删除原孤立的隐藏 modelName，把 modelName 放入底部中间 FrameLayout（bg_main_bottom_center 之上），match_parent + gravity=center 显示模式名。
- 注意：PluseMode=0（未收到设备数据）时不 setText，需设备返回 PluseMode 后才显示。

## 主界面点击启动下发工作状态（准备状态）
- 新增属性 `PadSAttribute.WorkState`（"WorkState"）。
- DeviceLauncherActivity：常量 WORK_STATE_PREPARING=1（准备状态）。
- 点击启动（rlStart，Launch==2 时进入 startPrepareCountdown）时，新增 sendWorkState(WORK_STATE_PREPARING)
  通过 MQTT 下发 WorkState=1，告知设备进入准备状态；随后本地开始准备倒计时(827)，
  倒计时结束 finishPrepareCountdown 再下发 Launch=1 正式启动。

## Launch 增加准备状态 (Launch==3) —— 修正前述 WorkState 方案
- 设备模式 Launch：0 暂停 / 1 启动 / 2 停止 / 3 准备（新增，准备=启动倒计时中）。
- 已撤销之前新增的 PadSAttribute.WorkState（改用 Launch=3 表达准备状态）。
- DeviceLauncherActivity 启动按钮(rlStart)逻辑：
  - Launch==1 → 发 0（暂停）；Launch==0 → 发 1（启动）；
  - Launch==2（停止）→ 发 Launch=3（准备）并 startPrepareCountdown()，按 prepareSeconds(来自 GetReadySecond) 开始倒计时；
  - 倒计时结束 finishPrepareCountdown() 发 Launch=1 正式启动。
- setLaunch() 增加 Launch==3 分支（状态文案"准备"）；cancelPrepareCountdown 改为 Launch!=3 时取消。
- 移除"点击模式后自动开启倒计时"功能：
  - 删除 EXTRA_AUTO_PREPARE_COUNTDOWN 常量、autoPrepareCountdown/waitingForReadySecond 字段、
    requestAutoPrepareCountdown()、setListener 中 autoPrepareCountdown 触发、GetReadySecond 中的 waiting 分支。
  - DeviceModelActivity / MaiChongSettingActivity 跳转 DeviceLauncherActivity 时不再 putExtra。
  - 更新 DeviceStartFlowStructureTest 对应用例。
