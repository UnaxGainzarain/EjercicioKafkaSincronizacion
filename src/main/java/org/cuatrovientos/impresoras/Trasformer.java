package org.cuatrovientos.impresoras;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cuatrovientos.impresoras.Document.ModoImpresion;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Trasformer implements Runnable {

    @Override
    public void run() {
        
        Properties propsCons = new Properties();
        propsCons.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        propsCons.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-transformadores"); 
        
        propsCons.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsCons.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DocumentDeserializer.class.getName());

        KafkaConsumer<String, Document> consumer = new KafkaConsumer<>(propsCons);
        consumer.subscribe(Collections.singletonList("topic-recepcion"));


     
        Properties propsProd = new Properties();
        propsProd.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        propsProd.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propsProd.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(propsProd);

        System.out.println("Transformador iniciado en hilo: " + Thread.currentThread().getName());

        try {
            while (true) {
                ConsumerRecords<String, Document> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, Document> record : records) {
                    Document doc = record.value();
                    System.out.println("[Transformador] Procesando documento: " + doc.getTitle());

                    procesarYEnviar(doc, producer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
            producer.close();
        }
    }

    private void procesarYEnviar(Document doc, KafkaProducer<String, String> producer) {
        String textoCompleto = doc.getContent();
        int longitud = textoCompleto.length();
        int tamanoPagina = 400;

        int totalPaginas = (int) Math.ceil((double) longitud / tamanoPagina);

        String topicDestino;
        if (doc.getModo() == ModoImpresion.COLOR) {
            topicDestino = "topic-color";
        } else {
            topicDestino = "topic-bn";
        }

        for (int i = 0; i < totalPaginas; i++) {
            int inicio = i * tamanoPagina;
            int fin = Math.min(inicio + tamanoPagina, longitud);
            
            String contenidoPagina = textoCompleto.substring(inicio, fin);
            
            String mensajeParaImpresora = String.format(
                "DOC: '%s' [Pág %d/%d] -> %s", 
                doc.getTitle(), (i + 1), totalPaginas, contenidoPagina
            );

            producer.send(new ProducerRecord<>(topicDestino, mensajeParaImpresora));
            
            System.out.println("   -> Enviada página " + (i + 1) + " a " + topicDestino);
        }
    }
}