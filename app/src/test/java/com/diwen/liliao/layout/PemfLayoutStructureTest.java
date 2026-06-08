package com.diwen.liliao.layout;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PemfLayoutStructureTest {

    @Test
    public void pemfContentUsesCompactFixedWidth() throws Exception {
        Document document = readPemfLayout();
        Element content = findById(document.getDocumentElement(), "@+id/pemfContent");
        Element settingsCard = findById(document.getDocumentElement(), "@+id/pemfSettingsCard");
        Element runningImage = findById(document.getDocumentElement(), "@+id/ivRunning");
        Element controlsRow = findById(document.getDocumentElement(), "@+id/pemfControlsRow");
        Element autoButton = findById(document.getDocumentElement(), "@+id/llPemfAuto");
        Element manualButton = findById(document.getDocumentElement(), "@+id/btnPemfManual");

        assertNotNull(content);
        assertNotNull(settingsCard);
        assertNotNull(runningImage);
        assertNotNull(controlsRow);
        assertNotNull(autoButton);
        assertNotNull(manualButton);
        assertEquals("220dp", content.getAttribute("android:layout_width"));
        assertEquals("match_parent", settingsCard.getAttribute("android:layout_width"));
        assertEquals("12dp", settingsCard.getAttribute("android:paddingHorizontal"));
        assertEquals("220dp", runningImage.getAttribute("android:layout_width"));
        assertEquals("220dp", controlsRow.getAttribute("android:layout_width"));
        assertEquals("88dp", autoButton.getAttribute("android:layout_width"));
        assertEquals("88dp", manualButton.getAttribute("android:layout_width"));
    }

    @Test
    public void pemfFieldsCanShrinkWithoutClippingUnits() throws Exception {
        Document document = readPemfLayout();
        Element frequency = findById(document.getDocumentElement(), "@+id/evFrequency");
        Element treatmentTime = findById(document.getDocumentElement(), "@+id/evTreatmentTime");

        assertNotNull(frequency);
        assertNotNull(treatmentTime);
        assertEquals("0dp", frequency.getAttribute("android:minWidth"));
        assertEquals("0dp", treatmentTime.getAttribute("android:minWidth"));
    }

    @Test
    public void intensityButtonsAreCompactEnoughForPemfCard() throws Exception {
        Document document = readIntensityButtonLayout();
        Element button = findById(document.getDocumentElement(), "@+id/tvButton");

        assertNotNull(button);
        assertEquals("22dp", button.getAttribute("android:layout_width"));
        assertEquals("22dp", button.getAttribute("android:layout_height"));
        assertEquals("4dp", button.getAttribute("android:layout_marginLeft"));
    }

    @Test
    public void pemfLayoutDoesNotExposeSaveButton() throws Exception {
        Document document = readPemfLayout();

        assertNull(findById(document.getDocumentElement(), "@+id/tvSave"));
    }

    @Test
    public void pemfControlsPlaceManualActionLeftAndAutoSwitchRight() throws Exception {
        Document document = readPemfLayout();
        Element controlsRow = findById(document.getDocumentElement(), "@+id/pemfControlsRow");
        Element autoButton = findById(document.getDocumentElement(), "@+id/llPemfAuto");
        Element manualText = findById(document.getDocumentElement(), "@+id/tvPemfManual");

        assertNotNull(controlsRow);
        assertNotNull(autoButton);
        assertNotNull(manualText);
        assertEquals("center_vertical", controlsRow.getAttribute("android:gravity"));
        assertEquals("@+id/btnPemfManual", childElementAt(controlsRow, 0).getAttribute("android:id"));
        assertEquals("@+id/llPemfAuto", childElementAt(controlsRow, 1).getAttribute("android:id"));
        assertEquals("@+id/tvAutoLabel", childElementAt(autoButton, 0).getAttribute("android:id"));
        assertEquals("@+id/pemfAutoSwitchTrack", childElementAt(autoButton, 1).getAttribute("android:id"));
        assertEquals("gone", manualText.getAttribute("android:visibility"));
    }

    @Test
    public void runningDiagramUsesCardBackground() throws Exception {
        Document document = readPemfLayout();
        Element runningImage = findById(document.getDocumentElement(), "@+id/ivRunning");

        assertNotNull(runningImage);
        assertEquals("@drawable/bg_pemf_running_panel", runningImage.getAttribute("android:background"));
    }

    @Test
    public void runningDiagramHasInnerPadding() throws Exception {
        Document document = readPemfLayout();
        Element runningImage = findById(document.getDocumentElement(), "@+id/ivRunning");

        assertNotNull(runningImage);
        assertEquals("12dp", runningImage.getAttribute("android:paddingHorizontal"));
        assertEquals("8dp", runningImage.getAttribute("android:paddingVertical"));
    }

    @Test
    public void intensityButtonsAlignToCardRightEdge() throws Exception {
        Document document = readPemfLayout();
        Element intensityRow = findById(document.getDocumentElement(), "@+id/pemfIntensityRow");
        Element spacer = findById(document.getDocumentElement(), "@+id/pemfIntensitySpacer");
        Element buttons = findById(document.getDocumentElement(), "@+id/rectangle");

        assertNotNull(intensityRow);
        assertNotNull(spacer);
        assertNotNull(buttons);
        assertEquals("@+id/tvIntensity", childElementAt(intensityRow, 0).getAttribute("android:id"));
        assertEquals("@+id/pemfIntensitySpacer", childElementAt(intensityRow, 1).getAttribute("android:id"));
        assertEquals("@+id/rectangle", childElementAt(intensityRow, 2).getAttribute("android:id"));
        assertEquals("0dp", spacer.getAttribute("android:layout_width"));
        assertEquals("1", spacer.getAttribute("android:layout_weight"));
        assertEquals("", buttons.getAttribute("android:layout_marginStart"));
    }

    private Document readPemfLayout() throws Exception {
        File layoutFile = new File("src/main/res/layout/fragment_pemf.xml");
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(layoutFile);
    }

    private Document readIntensityButtonLayout() throws Exception {
        File layoutFile = new File("src/main/res/layout/item_buttonairsettings.xml");
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(layoutFile);
    }

    private Element findById(Element element, String id) {
        if (id.equals(element.getAttribute("android:id"))) {
            return element;
        }
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element result = findById((Element) child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Element childElementAt(Element element, int targetIndex) {
        int elementIndex = 0;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!(child instanceof Element)) {
                continue;
            }
            if (elementIndex == targetIndex) {
                return (Element) child;
            }
            elementIndex++;
        }
        return null;
    }
}
