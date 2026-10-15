package co.edu.poli.examen2_Soto.tests.unitaria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class TestFormularioTarjeta {

    @Test
    void fxmlDeclaraSelectorObligatorioYControlador() throws Exception {
        try (InputStream fxml = getClass().getResourceAsStream(
                "/co/edu/poli/examen2_Soto/formCard.fxml")) {
            assertNotNull(fxml);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fxml);

            assertEquals("co.edu.poli.examen2_Soto.controlador.ControlFormCard",
                    document.getDocumentElement().getAttribute("fx:controller"));

            NodeList selectors = document.getElementsByTagName("ComboBox");
            boolean hasRequiredStatus = false;
            for (int index = 0; index < selectors.getLength(); index++) {
                Element selector = (Element) selectors.item(index);
                if ("cmbEstado".equals(selector.getAttribute("fx:id"))) {
                    hasRequiredStatus = "Seleccione estado".equals(selector.getAttribute("promptText"));
                }
            }

            assertTrue(hasRequiredStatus);
            assertEquals(3, co.edu.poli.examen2_Soto.modelo.EstadoTarjeta.values().length);
        }
    }
}