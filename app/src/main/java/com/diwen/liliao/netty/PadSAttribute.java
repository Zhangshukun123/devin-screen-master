package com.diwen.liliao.netty;

/**
 * Created By  tian on 2024/7/19
 * Describe:
 */
public enum PadSAttribute {
    UsageTime("UsageTime"),
    EndTime("EndTime"),
    StartTime("StartTime"),
    Number("Number"),
    TotalTime("TotalTime"),
    LedStatus("LedStatus"),
    ManualMode("ManualMode"),
    IntelligentMode("IntelligentMode"),
    RecordQuery("RecordQuery"),
    BoostCollagen("BoostCollagen"),
    PluseMode("PluseMode"),
    BobySim("BobySim"),
    PainRelief("PainRelief"),
    Fitness("Fitness"),
    Launch("Launch"),
    PulseSet("PulseSet"),
    PulseSetting("PulseSetting"),
    PulseSetting0("PulseSetting0"),
    PressKey("PressKey"),
    OutState("OutState"),
    PulseDuty("PulseDuty"),
    PulseDuty0("PulseDuty0"),
    PulseDuty1("PulseDuty1"),
    PulseDuty2("PulseDuty2"),
    PulseDuty3("PulseDuty3"),
    PulseDuty4("PulseDuty4"),
    PulseDuty5("PulseDuty5"),
    PulseSetting1("PulseSetting1"),
    PulseSetting2("PulseSetting2"),
    PulseSetting3("PulseSetting3"),
    PulseSetting4("PulseSetting4"),
    PulseSetting5("PulseSetting5"),
    AirT2Temp("AirT2Temp"),
    AirT1Temp("AirT1Temp"),
    AirAlarm("AirAlarm"),
    AirTemp("AirTemp"),
    AirTime("AirTime"),
    Language("Language"),
    RSSI("RSSI"),
    BtName("BtName"),
    BtState("BtState"),
    CutSong("CutSong"),
    onLineState("onlinestate"),
    DeviceTimeMin("DeviceTimeMin"),
    DeviceTimeSecond("DeviceTimeSecond"),
    Volume("Volume"),
    MusicalState("MusicalState"),
    BackKey("BackKey"),
    AirBlowerStop("AirBlowerStop"),
    Year("Year"),
    Month("Month"),
    Day("Day"),
    Hour("Hour"),
    Minute("Minute"),
    Second("Second"),
    AirBlowerRun("AirBlowerRun");

    // 成员变量  
    private String Attribute;

    private PadSAttribute(String Attribute) {
        this.Attribute = Attribute;
    }

    public String getAttribute() {
        return Attribute;
    }

    public void setAttribute(String attribute) {
        Attribute = attribute;
    }

} 
