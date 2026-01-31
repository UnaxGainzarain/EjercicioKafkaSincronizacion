package org.cuatrovientos.impresoras;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class Kafka {

	private static Kafka instancia;
    private KafkaProducer<String, Document> producer;
    
   
    private final String TOPIC_ENTRADA = "topic-recepcion";

    private Kafka() {
        Properties props = new Properties();
        
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DocumentSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public static synchronized Kafka obtenerInstancia() {
        if (instancia == null) {
            instancia = new Kafka();
        }
        return instancia;
    }

    
    public void enviarOriginal(Document documento) {
        
        ProducerRecord<String, Document> record = new ProducerRecord<>(
            TOPIC_ENTRADA,      
            documento.getSender(),
            documento              
        );

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error enviando documento de: " + documento.getSender());
                exception.printStackTrace();
            } else {
                System.out.println(" Documento enviado a Kafka (Offset " + metadata.offset() + ")");
            }
        });
    }

    public void cerrar() {
        if (producer != null) {
            producer.close();
            System.out.println("Conexión con Kafka cerrada.");
        }
    }
	}


