package org.cuatrovientos.impresoras;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cuatrovientos.impresoras.Document.ModoImpresion;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Trasformer {

    public static void main(String[] args) {
        Properties propsConsumer = new Properties();
        propsConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-transformadores"); // ¡Importante!
        propsConsumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DocumentDeserializer.class.getName());

        KafkaConsumer<String, Document> consumer = new KafkaConsumer<>(propsConsumer);
        consumer.subscribe(Collections.singletonList("topic-recepcion"));

        
        Properties propsProducer = new Properties();
        propsProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propsProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(propsProducer);

        System.out.println("Transformador iniciado. Esperando documentos...");

        while (true) {
            ConsumerRecords<String, Document> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, Document> record : records) {
                Document doc = record.value();
                System.out.println("Procesando documento: " + doc.getTitle());

                int tamanoPagina = 400;
                String texto = doc.getContent();
                int totalLength = texto.length();
                
                int paginas = (int) Math.ceil((double) totalLength / tamanoPagina);

                String topicDestino = (doc.getModo() == ModoImpresion.COLOR) ? "topic-color" : "topic-bn";

                for (int i = 0; i < paginas; i++) {
                    int start = i * tamanoPagina;
                    int end = Math.min(start + tamanoPagina, totalLength);
                    
                    String contenidoPagina = texto.substring(start, end);
                    String mensajeFinal = "Doc: " + doc.getTitle() + " [Pag " + (i+1) + "]: " + contenidoPagina;

                    producer.send(new ProducerRecord<>(topicDestino, mensajeFinal));
                    System.out.println(" -> Página " + (i+1) + " enviada a " + topicDestino);
                }
            }
        }
    }
}

	
