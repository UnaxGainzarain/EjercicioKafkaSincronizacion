package org.cuatrovientos.impresoras;

import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocumentDeserializer implements Deserializer<Document> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Document deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;
            return objectMapper.readValue(data, Document.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al recibir el documento", e);
        }
    }
}