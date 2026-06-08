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

public class FansLayoutStructureTest {

    @Test
    public void gearButtonsAlignToHumanFanCardRightEdge() throws Exception {
        Document document = readFansLayout();
        Element humanCard = findById(document.getDocumentElement(), "@+id/fanHumanCard");
        Element gearRow = findById(document.getDocumentElement(), "@+id/fanGearRow");
        Element spacer = findById(document.getDocumentElement(), "@+id/fanGearSpacer");
        Element buttons = findById(document.getDocumentElement(), "@+id/rectangle");

        assertNotNull(humanCard);
        assertNotNull(gearRow);
        assertNotNull(spacer);
        assertNotNull(buttons);
        assertEquals("16dp", humanCard.getAttribute("android:paddingHorizontal"));
        assertEquals("@+id/tvChiLun", childElementAt(gearRow, 0).getAttribute("android:id"));
        assertEquals("@+id/fanGearSpacer", childElementAt(gearRow, 1).getAttribute("android:id"));
        assertEquals("@+id/rectangle", childElementAt(gearRow, 2).getAttribute("android:id"));
        assertEquals("0dp", spacer.getAttribute("android:layout_width"));
        assertEquals("1", spacer.getAttribute("android:layout_weight"));
    }

    private Document readFansLayout() throws Exception {
        File layoutFile = new File("src/main/res/layout/fragment_fans.xml");
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
