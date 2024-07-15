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

    // Identificador de la ventana
    private long window;
    // Posiciones y rotaciones de la cámara
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = -7.0f;
    private float cameraRotationX = 0.0f;
    private float cameraRotationY = 0.0f;

    public void run() {
        // Imprimir versión de LWJGL
        System.out.println("¡LWJGL " + Version.getVersion() + " funcionando!");

        try {
            // Inicializar y entrar en el bucle principal
            init();
            loop();
        } finally {
            // Liberar los recursos de GLFW
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        // Configurar la devolución de llamada de error
        GLFWErrorCallback.createPrint(System.err).set();

        // Inicializar GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo inicializar GLFW");
        }

        // Configurar la ventana GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // La ventana será invisible al principio
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // La ventana no será redimensionable

        // Crear la ventana
        window = glfwCreateWindow(800, 600, "Luces", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("No se pudo crear la ventana de GLFW");
        }

        // Configurar la posición de la ventana centrada en el monitor
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Hacer el contexto OpenGL actual
        glfwMakeContextCurrent(window);
        // Habilitar v-sync
        glfwSwapInterval(1);
        // Hacer visible la ventana
        glfwShowWindow(window);

        // Configurar la devolución de llamada del teclado
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

    private void loop() {
        // Crear capacidades de OpenGL
        GL.createCapabilities();

        // Configurar el color de limpieza del búfer
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Habilitar la prueba de profundidad
        glEnable(GL_DEPTH_TEST);

        // Configurar la proyección
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float) 800 / 600;
        gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f);

        // Cambiar a la matriz de modelo/vista
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Configurar la iluminación
        setupLighting();

        // Bucle principal de renderizado
        while (!glfwWindowShouldClose(window)) {
            // Limpiar el búfer de color y profundidad
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Reiniciar la matriz de modelo/vista
            glLoadIdentity();
            // Aplicar transformaciones de la cámara
            glTranslatef(cameraX, cameraY, cameraZ);
            glRotatef(cameraRotationX, 1.0f, 0.0f, 0.0f);
            glRotatef(cameraRotationY, 0.0f, 1.0f, 0.0f);

            // Dibujar la escena
            drawScene();

            // Intercambiar los búferes y procesar eventos
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void setupLighting() {
        // Habilitar la iluminación y el material de color
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);

        // Configurar la luz 0
        glEnable(GL_LIGHT0);
        float[] lightPos = {0.0f, 5.0f, -5.0f, 1.0f};
        glLightfv(GL_LIGHT0, GL_POSITION, lightPos);
        float[] ambientLight = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuseLight = {0.8f, 0.8f, 0.8f, 1.0f};
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);
    }

    private void drawScene() {
        // Dibujar el plano y el cubo
        drawPlane();
        drawCube();
    }

    private void drawPlane() {
        // Dibujar un plano escalado para representar el suelo
        glPushMatrix();
        glTranslatef(0.0f, -1.0f, 0.0f);
        glScalef(10.0f, 0.1f, 10.0f);
        glColor3f(0.6f, 0.6f, 0.6f);
        drawCube();
        glPopMatrix();
    }

    private void drawCube() {
        // Dibujar un cubo con diferentes colores en cada cara
        glBegin(GL_QUADS);

        // Cara frontal
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // Cara trasera
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        // Cara superior
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);

        // Cara inferior
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);

        // Cara derecha
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);

        // Cara izquierda
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);

        glEnd();
    }

    private void gluPerspective(float fov, float aspectRatio, float zNear, float zFar) {
        // Implementar la perspectiva con gluPerspective
        float ymax, xmax;
        ymax = (float) (zNear * Math.tan(Math.toRadians(fov / 2)));
        xmax = ymax * aspectRatio;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
    }
}
