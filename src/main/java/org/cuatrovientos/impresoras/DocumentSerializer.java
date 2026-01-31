package org.cuatrovientos.impresoras;


import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class DocumentSerializer implements Serializer<Document> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Document data) {
        try {
            if (data == null) return null;
            // Convierte el objeto Document a JSON (Bytes)
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando Document", e);
        }
    }
}