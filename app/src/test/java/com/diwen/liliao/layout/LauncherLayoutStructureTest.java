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

public class LauncherLayoutStructureTest {

    @Test
    public void fanControlFloatsAboveBottomBar() throws Exception {
        Document document = readLauncherLayout();
        Element fanControl = findById(document.getDocumentElement(), "@+id/fan_control");

        assertNotNull(fanControl);
        assertEquals("@id/ll_bottom", fanControl.getAttribute("app:layout_constraintBottom_toTopOf"));
        assertEquals("parent", fanControl.getAttribute("app:layout_constraintStart_toStartOf"));
        assertEquals("parent", fanControl.getAttribute("app:layout_constraintEnd_toEndOf"));
    }

    @Test
    public void modelNameIsHiddenOutsideBottomBar() throws Exception {
        Document document = readLauncherLayout();
        Element llBottom = findById(document.getDocumentElement(), "@+id/ll_bottom");
        Element modelName = findById(document.getDocumentElement(), "@+id/modelName");

        assertNotNull(llBottom);
        assertNotNull(modelName);
        assertEquals("gone", modelName.getAttribute("android:visibility"));
        assertEquals(false, containsId(llBottom, "@+id/modelName"));
    }

    private Document readLauncherLayout() throws Exception {
        File layoutFile = new File("src/main/res/layout/layout_devicelauncheractivity.xml");
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

    private boolean containsId(Element element, String id) {
        return findById(element, id) != null;
    }
}
