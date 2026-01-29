import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
public class TrasformadorService {
	public static void main(String[] args) {
        // 1. Configurar Consumidor (Input)
        Properties consProps = new Properties();
        consProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "grupo-transformacion"); 

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps);
        consumer.subscribe(Collections.singletonList("trabajos-entrada"));

        // 2. Configurar Productor (Output)
        Properties prodProps = new Properties();
        prodProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        prodProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prodProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(prodProps);

        System.out.println(">> Transformador iniciado...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                procesar(record.value(), producer);
            }
        }
    }

    private static void procesar(String json, KafkaProducer<String, String> producer) {
        try {
            // Extracción precaria de datos (simulando parsing manual)
            String tipo = json.contains("Color") ? "Color" : "BN";
            String sender = json.split("\"sender\":")[1].split("\"")[1];
            
            // Lógica de división de páginas (Dummy)
            // Aquí dividiríamos el texto en trozos de 400 chars.
            String contenidoProcesado = "Documento procesado y paginado para " + sender; 

            // Decidir Topic destino
            String topicDestino = tipo.equals("Color") ? "cola-impresion-color" : "cola-impresion-bn";

            // ENVIAR A LA COLA DE IMPRESIÓN
            // Usamos 'sender' como KEY para que siempre vaya a la misma partición (impresora) 
            ProducerRecord<String, String> envio = new ProducerRecord<>(topicDestino, sender, contenidoProcesado);
            
            producer.send(envio);
            System.out.println(">> Transformador: Redirigido trabajo de " + sender + " a " + topicDestino);
            
        } catch (Exception e) {
            System.err.println("Error transformando: " + e.getMessage());
        }
    }
}
