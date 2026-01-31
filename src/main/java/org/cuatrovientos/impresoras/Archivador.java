package org.cuatrovientos.impresoras;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Archivador implements Runnable { 

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-archivadores"); 
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DocumentDeserializer.class.getName());

        KafkaConsumer<String, Document> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("topic-recepcion"));

        ObjectMapper objectMapper = new ObjectMapper(); 

        System.out.println(" Archivador iniciado en hilo: " + Thread.currentThread().getName());

        try {
            while (true) {
                ConsumerRecords<String, Document> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, Document> record : records) {
                    Document doc = record.value();
                    System.out.println(" [Archivador] Guardando copia de: " + doc.getSender());
                    
                    guardarEnCarpetaSender(doc, objectMapper);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    private void guardarEnCarpetaSender(Document doc, ObjectMapper mapper) {
        File directorio = new File("archivados/" + doc.getSender());
        if (!directorio.exists()) directorio.mkdirs();

        File archivo = new File(directorio, doc.getTitle() + ".json");

        try {
            mapper.writeValue(archivo, doc);
        } catch (IOException e) {
            System.err.println("Error guardando JSON: " + e.getMessage());
        }
    }
}