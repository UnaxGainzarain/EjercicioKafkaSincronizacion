package org.cuatrovientos.impresoras;

import org.cuatrovientos.impresoras.Document.ModoImpresion;

public class Office {

	public static void main(String[] args) {
		System.out.println(" INICIANDO SISTEMA DE LA OFICINA");

		System.out.println("Arrancando servicios en segundo plano");

		new Thread(new Archivador()).start();
		new Thread(new Trasformer()).start();

		for (int i = 1; i <= 3; i++) {
			new Thread(new ImpresoraSimulada(i, ModoImpresion.BLANCO_NEGRO)).start();
		}
		for (int i = 1; i <= 2; i++) {
			new Thread(new ImpresoraSimulada(i, ModoImpresion.COLOR)).start();
		}

		esperar(2000);
		System.out.println(" INFRAESTRUCTURA LISTA. ESPERANDO TRABAJOS.\n");

		Enployees miguel = new Enployees("Miguel Goyena");
		Enployees ana = new Enployees("Ana Lopez");

		System.out.println(">>>Miguel está enviando un acta de reunión...");
		miguel.mandarImprimir("Acta Reunión", "Asistentes: Todo el equipo. Temas: Kafka y Java.",
				ModoImpresion.BLANCO_NEGRO);

		esperar(3000);
		System.out.println("\n>>> Ana envía un diseño gráfico MUY GRANDE...");

		String textoLargo = "A".repeat(300) + " [FIN PAG 1] " + "B".repeat(300) + " [FIN PAG 2]";

		ana.mandarImprimir("Cartel Publicitario", textoLargo, ModoImpresion.COLOR);

		esperar(3000);

		System.out.println("\n Jornada laboral terminada (Simulación de envío finalizada).");

		Kafka.obtenerInstancia().cerrar();

	}

	private static void esperar(int milisegundos) {
		try {
			Thread.sleep(milisegundos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
