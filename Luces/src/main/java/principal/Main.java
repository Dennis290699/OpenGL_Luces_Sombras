package principal;

import model.Luces;
import model.Simulacion_Plano;
import model.Simulacion_Simple;

public class Main {

	public static void main(String[] args) {
		// Establecer la propiedad del sistema para la ruta de la biblioteca LWJGL
	    System.setProperty("org.lwjgl.librarypath", "src/main/resources/ogl");

	    // Ejecutar la simulación deseada
	    // Descomenta una de las siguientes líneas para ejecutar la simulación correspondiente

	    // Ejecutar la simulación de luces
	    // new Luces().run();

	    // Ejecutar la simulación simple
	    // new Simulacion_Simple().run();

	    // Ejecutar la simulación del plano
	    new Simulacion_Plano().run();
	}
}