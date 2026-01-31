package org.cuatrovientos.impresoras;

import java.util.ArrayList;
import java.util.List;

import org.cuatrovientos.impresoras.Document.ModoImpresion;

public class Enployees {
	public String nombre;
	public List<Document> listaDocumentos;

	public Enployees(String nombre) {
		super();
		this.nombre = nombre;
		this.listaDocumentos = new ArrayList<>();
	}

	public void mandarImprimir(String titulo, String contenido, ModoImpresion modo) {

		System.out.println(this.nombre + " está creando el documento...");

		Document doc = new Document(this.nombre, titulo, contenido, modo);

		Kafka.obtenerInstancia().enviarOriginal(doc);
	}
}
