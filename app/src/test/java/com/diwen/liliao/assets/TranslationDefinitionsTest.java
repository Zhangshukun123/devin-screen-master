package com.diwen.liliao.assets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TranslationDefinitionsTest {

    @Test
    public void calibratedWorkbookTermsAreUsedInEveryLanguage() throws Exception {
        Map<String, String[]> expected = new LinkedHashMap<>();
        expected.put("zh.json", new String[]{"工作模式", "智能模式", "人体循环风机", "进入仓内", "强度", "治疗时间"});
        expected.put("en.json", new String[]{"Operating Mode", "Smart Mode", "Body Cooling Fan", "Enter the Chamber Now", "Intensity", "Treatment Time"});
        expected.put("fr.json", new String[]{"Mode de fonctionnement", "Mode intelligent", "Ventilation corporelle", "Entrez maintenant dans la chambre", "Intensité", "Temps de traitement"});
        expected.put("de.json", new String[]{"Betriebsmodus", "Smart-Modus", "Körperkühlung", "Betreten Sie jetzt die Kammer", "Intensität", "Behandlungszeit"});
        expected.put("it.json", new String[]{"Modalità operativa", "Modalità intelligente", "Ventilazione corpo", "Entra ora nella camera", "Intensità", "Tempo di trattamento"});
        expected.put("es.json", new String[]{"Modo de funcionamiento", "Modo inteligente", "Ventilación corporal", "Entre ahora en la cámara", "Intensidad", "Tiempo de tratamiento"});

        String[] keys = {"工作模式", "智能模式", "人体循环风机", "进入仓内", "强度", "治疗时间"};
        for (Map.Entry<String, String[]> entry : expected.entrySet()) {
            JSONObject translations = readTranslations(entry.getKey());
            for (int i = 0; i < keys.length; i++) {
                assertEquals(entry.getKey() + " / " + keys[i], entry.getValue()[i], translations.getString(keys[i]));
            }
        }
    }

    private JSONObject readTranslations(String fileName) throws Exception {
        File file = new File("src/main/assets", fileName);
        String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return JSON.parseObject(json);
    }
}
