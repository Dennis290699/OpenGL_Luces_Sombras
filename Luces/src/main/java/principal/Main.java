package principal;

import model.Luces;
import model.Simulacion_Plano;
import model.Simulacion_Simple;

public class Main {

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", "src/main/resources/ogl");
//		new Luces().run();
//		new Simulacion_Simple().run();
		new Simulacion_Plano().run();
	}
}