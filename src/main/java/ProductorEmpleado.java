import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
public class ProductorEmpleado {
	private static final String TOPIC = "trabajos-entrada";

    public static void main(String[] args) {
        Properties props = new Properties();
        // Configuración básica del productor [cite: 154]
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
     // Simulamos el JSON requerido
        String jsonTrabajo = "{"
                + "\"titulo\": \"Informe Trimestral\","
                + "\"documento\": \"Este es el contenido del documento que es muy largo y debe ser dividido en paginas de 400 caracteres para poder ser impreso correctamente por las impresoras antiguas...\"," 
                + "\"tipo\": \"B/N\","
                + "\"sender\": \"Unax Osaba\""
                + "}";
        
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "Unax Osaba", jsonTrabajo);
        
        try {
            producer.send(record);
            System.out.println(">> Empleado: Trabajo enviado a " + TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close(); // [cite: 165]
        }
    }
}
