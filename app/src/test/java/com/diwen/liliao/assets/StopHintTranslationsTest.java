package com.diwen.liliao.assets;

import com.alibaba.fastjson.JSON;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StopHintTranslationsTest {

    @Test
    public void everySupportedLanguageHasExactHoldToStopText() throws Exception {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("zh.json", "长按3秒停止");
        expected.put("en.json", "HOLD 3S TO STOP");
        expected.put("fr.json", "MAINTENIR 3S POUR ARRÊTER");
        expected.put("de.json", "3S DRÜCKEN ZUM STOPPEN");
        expected.put("it.json", "TENERE 3S PER FERMARE");
        expected.put("es.json", "MANTENER 3S PARA DETENER");

        for (Map.Entry<String, String> entry : expected.entrySet()) {
            File file = new File("src/main/assets", entry.getKey());
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            assertEquals(entry.getValue(), JSON.parseObject(json).getString("长按3秒停止"));
        }
    }
}
