package org.cuatrovientos.impresoras;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Archivador {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// IMPORTANTE: Group ID único para archivado [cite: 190]
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "grupo-archivado");
		props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("trabajos-entrada"));

		System.out.println(">> Archivador iniciado. Esperando mensajes...");

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // [cite: 277]
			for (ConsumerRecord<String, String> record : records) {
				guardarEnDisco(record.value());
			}
		}
	}

	private static void guardarEnDisco(String json) {
		try {
			// Extraer sender 
			
			String sender = "desconocido";
			if (json.contains("\"sender\":")) {
				sender = json.split("\"sender\":")[1].split("\"")[1];
			}

			// Crear carpeta por sender
			File carpeta = new File("backup_archivos/" + sender);
			if (!carpeta.exists())
				carpeta.mkdirs();

			File file = new File(carpeta, "doc_" + System.currentTimeMillis() + ".json");
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(json);
			}
			System.out.println(">> Archivador: Guardado backup de " + sender);
		} catch (Exception e) {
			System.err.println("Error archivando: " + e.getMessage());
		}
	}

}
