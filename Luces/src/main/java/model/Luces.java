package model;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Luces {

	private long window;
	private float cameraX = 0.0f; // posicion de la camara en el eje x
	private float cameraY = 0.0f; // posicion de la camara en el eje y
	private float cameraZ = -7.0f; // posicion de la camara en el eje z
	private float cameraRotationX = 0.0f; // angulo de rotacion de la camara en el eje x
	private float cameraRotationY = 0.0f; // angulo de rotacion de la camara en el eje x

	// inicializacion de la ventana y objetos mediante metodos
	public void run() {
		System.out.println("¡LWJGL " + Version.getVersion() + " funcionando!"); // version del lwjgl
		try {
			init(); // metodo que se inicia una unica veez
			loop(); // constante redibujado o blucle donde los objetos se dibujan una y otra vez
		} finally { // liberacion de recursos
			glfwFreeCallbacks(window); // liberar los recursos de la ventana que creeas
			glfwDestroyWindow(window); // destruyes la ventana
			glfwTerminate(); // terminar la libreria glfw para recuperar recursos
			glfwSetErrorCallback(null).free(); // posibles errores que existan
		}
	}

	// inicializador de objetos
	private void init() {
		//// ************************** lineas de codigo para atrapar un error en caso
		//// de que no se importe la libreri GLFW
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("No se pudo inicializar GLFW");
		}
////**************************
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // la ventana no puede ser modificada en tamaño, fija un solo tamaño

		window = glfwCreateWindow(800, 600, "Luces", NULL, NULL); // creacion de la ventana, la cual tiene 800pixeles de
																	// ancho y 600 de alto
		if (window == NULL) {
			throw new RuntimeException("No se pudo crear la ventana de GLFW");
		} // atrapa errores en caso de que no se pueda crear la ventana

		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		// creacion de la ventana+++++++++++++++++++++++++++++++++
		// *******************************************************

		// metodo para poder atrapar la lectura de teclado
		// ver si una tecla es presionada
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS || action == GLFW_REPEAT) {
				switch (key) {
				case GLFW_KEY_W -> cameraZ += 0.1f;
				case GLFW_KEY_S -> cameraZ -= 0.1f;
				case GLFW_KEY_A -> cameraRotationY -= 5.0f;
				case GLFW_KEY_D -> cameraRotationY += 5.0f;
				case GLFW_KEY_UP -> cameraRotationX -= 5.0f;
				case GLFW_KEY_DOWN -> cameraRotationX += 5.0f;
				}
			}
		});
	}

//***********************************************
	// redibujar una y otra ves la pantalla y sus objetos
	private void loop() {
		GL.createCapabilities();

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // metodo para limpiar la pantalla y asignarle un color

		glEnable(GL_DEPTH_TEST); // agrega profundidad a la ventana, tus objetos se posen ensima de la ventana y
									// relleno de las figuras

		glMatrixMode(GL_PROJECTION); // visualicen los objetos

		glLoadIdentity(); // manejo de matrices interna

		float aspectRatio = (float) 800 / 600;

		gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f); // maneja perspectivas de camara

		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();

		setupLighting(); // configuracion de las luces del escenario metodo

		// dibujar una y otra ves los objetos
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glLoadIdentity();
			glTranslatef(cameraX, cameraY, cameraZ);
			glRotatef(cameraRotationX, 1.0f, 0.0f, 0.0f);
			glRotatef(cameraRotationY, 0.0f, 1.0f, 0.0f);

			drawCubes(); // dibujar el cubo en la ventana una y otra vez

			glfwSwapBuffers(window); // metodo para anexar los objetos a la ventana
			glfwPollEvents(); // manejo de eventos
		}
		//
	}

	// metodo es para quitar toda la luz del escenario para poder colocar
	// correctamente las luces
	private void setupLighting() {
		glEnable(GL_LIGHTING); // habilita luces
		glEnable(GL_COLOR_MATERIAL); // habilita las luces sobre los objetos

		// Luz ambiental global (opcional, para evitar que todo esté en oscuridad)
		glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f, 1.0f });
		glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] { 0.0f, 0.0f, 0.0f, 1.0f });
		glLightfv(GL_LIGHT0, GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f });
		glLightfv(GL_LIGHT0, GL_POSITION, new float[] { 0.0f, 0.0f, 0.0f, 1.0f });
		glDisable(GL_LIGHT0); // Desactivamos la luz ambiental global

		glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE); // asigna una luz ambiental y difusa sobre el material del
															// objeto

	}

	// dibujado del cubo y asignacion de las luces, modificar escalado
	private void drawCubes() {
		// Cubo con luz ambiental
		glPushMatrix(); // guardar las cordenadas del cubo
		glTranslatef(-2.5f, 0.0f, 0.0f); // Acercamos los cubos
		glScalef(0.75f, 0.75f, 0.75f); // Hacemos el cubo un poco más grande
		setupAmbientLight(); // asignar el tipo de luz que el objeto va a tener
		drawCube();
		glPopMatrix(); // cualquier transformacion hecha en la matriz de cordenadas son revertidas para
						// dejar a la matriz en su estado original

		// Cubo con luz difusa
		glPushMatrix();
		glTranslatef(0.0f, 0.0f, 0.0f); // Posición central
		glScalef(0.75f, 0.75f, 0.75f); // Hacemos el cubo un poco más grande
		setupDiffuseLight(); // La luz difusa ilumina los objetos basándose en el ángulo de incidencia de la
								// luz sobre las superficies.
		drawCube();
		glPopMatrix();

		// Cubo con luz especular
		glPushMatrix();
		glTranslatef(2.5f, 0.0f, 0.0f); // Acercamos los cubos
		glScalef(0.75f, 0.75f, 0.75f); // Hacemos el cubo un poco más grande
		setupSpecularLight();// La luz especular crea reflejos brillantes en la superficie basada en el
								// ángulo de reflexión.
		drawCube();
		glPopMatrix();
	}

	// configuracion de la luz ambiental en tono bajo
	private void setupAmbientLight() {
		glEnable(GL_LIGHT0); // inicializado de una luz
		float[] ambientLight = { 0.2f, 0.2f, 0.2f, 1.0f }; // parametros de intensidad de la luz
		glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight); // meetodo para definir tipo de luz
	}

	private void setupDiffuseLight() {
		glEnable(GL_LIGHT1);
		float[] diffuseLight = { 0.8f, 0.8f, 0.8f, 1.0f };
		glLightfv(GL_LIGHT1, GL_DIFFUSE, diffuseLight);
		glLightfv(GL_LIGHT1, GL_POSITION, new float[] { 0.0f, 1.5f, 1.0f, 1.0f }); // posiscion de la luz
	}

	private void setupSpecularLight() {
		glEnable(GL_LIGHT2);
		float[] specularLight = { 1.0f, 1.0f, 1.0f, 1.0f };
		glLightfv(GL_LIGHT2, GL_SPECULAR, specularLight);
		glLightfv(GL_LIGHT2, GL_POSITION, new float[] { 2.5f, 1.5f, 1.0f, 1.0f }); // posiscion de la luz
	}

	// dibujar el cubo mediante triangulacion
	private void drawCube() {
		glBegin(GL_QUADS); // define que se va a dibujar un cuadrado

		// delantera del cubo
		glColor3f(1.0f, 0.0f, 0.0f); // color del cubo Rojo
		glVertex3f(-1.0f, -1.0f, 1.0f);
		glVertex3f(1.0f, -1.0f, 1.0f);
		glVertex3f(1.0f, 1.0f, 1.0f);
		glVertex3f(-1.0f, 1.0f, 1.0f);

		// Back face
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(-1.0f, 1.0f, -1.0f);
		glVertex3f(1.0f, 1.0f, -1.0f);
		glVertex3f(1.0f, -1.0f, -1.0f);

		// Top face
		glVertex3f(-1.0f, 1.0f, -1.0f);
		glVertex3f(-1.0f, 1.0f, 1.0f);
		glVertex3f(1.0f, 1.0f, 1.0f);
		glVertex3f(1.0f, 1.0f, -1.0f);

		// Bottom face
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(1.0f, -1.0f, -1.0f);
		glVertex3f(1.0f, -1.0f, 1.0f);
		glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right face
		glVertex3f(1.0f, -1.0f, -1.0f);
		glVertex3f(1.0f, 1.0f, -1.0f);
		glVertex3f(1.0f, 1.0f, 1.0f);
		glVertex3f(1.0f, -1.0f, 1.0f);

		// Left face
		glVertex3f(-1.0f, -1.0f, -1.0f);
		glVertex3f(-1.0f, -1.0f, 1.0f);
		glVertex3f(-1.0f, 1.0f, 1.0f);
		glVertex3f(-1.0f, 1.0f, -1.0f);

		glEnd(); // termina el proceso de dibujado
	}

	private void gluPerspective(float fov, float aspectRatio, float zNear, float zFar) {
		float ymax, xmax;
		ymax = (float) (zNear * Math.tan(Math.toRadians(fov / 2)));
		xmax = ymax * aspectRatio;
		glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
	}
}// FINAL CLASS
