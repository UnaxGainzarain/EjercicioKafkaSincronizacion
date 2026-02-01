package org.cuatrovientos.impresoras;

import java.util.List;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.cuatrovientos.impresoras.Document.ModoImpresion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ImpresoraSimulada implements Runnable {
    private int id;
    private ModoImpresion tipo;

    public ImpresoraSimulada(int id, ModoImpresion tipo) {
        super();
        this.id = id;
        this.tipo = tipo;
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Todas las impresoras del mismo tipo comparten grupo para repartirse el
        // trabajo
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-impresoras-" + tipo);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = (tipo == ModoImpresion.COLOR) ? "topic-color" : "topic-bn";
        consumer.subscribe(Collections.singletonList(topic));

        System.out.println(
                "✅ Impresora " + tipo + " [" + id + "] ENCENDIDA en hilo: " + Thread.currentThread().getName());

        // Carpeta para guardar los ficheros
        File carpeta = new File("impresiones/" + tipo);
        if (!carpeta.exists())
            carpeta.mkdirs();

        try {
            while (true) {
                // Leemos mensajes
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(" [Hilo " + id + "] Imprimiendo: " + record.value());
                    guardarEnFichero(carpeta, record.value());

                    // Simular que tarda un poco en imprimir (500ms)
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    private void guardarEnFichero(File carpeta, String contenido) {
        String nombreFichero = System.currentTimeMillis() + "_imp" + id + ".txt";
        File archivo = new File(carpeta, nombreFichero);
        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
