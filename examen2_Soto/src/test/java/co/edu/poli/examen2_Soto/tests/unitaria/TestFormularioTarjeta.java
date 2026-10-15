package co.edu.poli.examen2_Soto.tests.unitaria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class TestFormularioTarjeta {

    private static final String FXML_NAMESPACE = "http://javafx.com/fxml/1";

    @Test
    void fxmlDeclaraLosControlesQueUsaElControlador() throws Exception {
        try (InputStream fxml = getClass().getResourceAsStream(
                "/co/edu/poli/examen2_Soto/formCard.fxml")) {
            assertNotNull(fxml);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(fxml);

            assertEquals("co.edu.poli.examen2_Soto.controlador.ControlFormCard",
                    document.getDocumentElement().getAttributeNS(FXML_NAMESPACE, "controller"));

            Element stateSelector = findByFxId(document, "cmbEstado");
            Element titularSelector = findByFxId(document, "cmbTitular");
            Element createButton = findByFxId(document, "bttCreacion");
            Element debitRadio = findByFxId(document, "radio1");

            assertNotNull(stateSelector);
            assertEquals("Seleccione estado", stateSelector.getAttribute("promptText"));
            assertNotNull(titularSelector);
            assertNotNull(findByFxId(document, "txtTarjeta2"));
            assertEquals(1, document.getElementsByTagName("DatePicker").getLength());
            assertNotNull(createButton);
            assertEquals("#pressCreacion", createButton.getAttribute("onAction"));
            assertNotNull(debitRadio);
            assertEquals("true", debitRadio.getAttribute("selected"));
            assertEquals(3, co.edu.poli.examen2_Soto.modelo.EstadoTarjeta.values().length);
        }
    }

    private Element findByFxId(Document document, String fxId) {
        NodeList elements = document.getElementsByTagName("*");
        for (int index = 0; index < elements.getLength(); index++) {
            Element element = (Element) elements.item(index);
            if (fxId.equals(element.getAttributeNS(FXML_NAMESPACE, "id"))) {
                return element;
            }
        }
        return null;
    }
}