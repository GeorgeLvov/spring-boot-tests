package com.example.springboottests.functional.note;

import com.example.springboottests.functional.note.exception.XmlConverterException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Utility component for XML conversion.
 * This component provides methods to deserialize XML data into Java objects.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Component
public class XmlConverter {

    private static final XmlMapper XML_MAPPER  = new XmlMapper();

    /**
     * Deserializes XML data into a Java object of the specified class.
     *
     * @param xml         The XML data to deserialize.
     * @param objectClass The class of the object to deserialize to.
     * @param <T>         The type of the object to deserialize to.
     * @return The deserialized Java object.
     * @throws XmlConverterException if an error occurs during the deserialization process.
     */
    public <T> T toObject(String xml, Class<T> objectClass) {
        try {
            Assert.notNull(xml, "XML that should be deserialized must not be null!");
            Assert.notNull(objectClass, "Object class as goal for deserialization must not be null!");

            log.debug("Start deserializing XML to {}...", objectClass.getSimpleName());

            T result = XML_MAPPER.readValue(xml, objectClass);

            log.debug("XML deserialization to {} was successful!", objectClass.getSimpleName());

            return result;
        } catch (Exception e) {
            throw new XmlConverterException("Failed to convert XML-message to object!", e);
        }
    }
}
