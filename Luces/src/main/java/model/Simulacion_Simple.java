package model;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Simulacion_Simple {

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

        // Crear capacidades de OpenGL
        GL.createCapabilities();

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

        // Configurar la devolución de llamada del teclado
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W:
                        cameraZ += 0.1f;
                        break;
                    case GLFW_KEY_S:
                        cameraZ -= 0.1f;
                        break;
                    case GLFW_KEY_A:
                        cameraRotationY -= 5.0f;
                        break;
                    case GLFW_KEY_D:
                        cameraRotationY += 5.0f;
                        break;
                    case GLFW_KEY_UP:
                        cameraRotationX -= 5.0f;
                        break;
                    case GLFW_KEY_DOWN:
                        cameraRotationX += 5.0f;
                        break;
                }
            }
        });
    }

    private void loop() {
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
            drawCubes();

            // Intercambiar los búferes y procesar eventos
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void setupLighting() {
        // Habilitar la iluminación y el material de color
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);

        // Configurar el modelo de iluminación local
        glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);

        // Configurar la luz ambiental
        float[] ambientLight = {0.2f, 0.2f, 0.2f, 1.0f};
        FloatBuffer ambientLightBuffer = BufferUtils.createFloatBuffer(4);
        ambientLightBuffer.put(ambientLight).flip();
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLightBuffer);

        // Habilitar la luz
        glEnable(GL_LIGHT0);
    }

    private void drawCubes() {
        // Dibujar múltiples cubos en diferentes posiciones
        glPushMatrix();
        glTranslatef(-2.5f, 0.0f, 0.0f);
        glScalef(0.75f, 0.75f, 0.75f);
        drawCube();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0.0f, 0.0f, 0.0f);
        glScalef(0.75f, 0.75f, 0.75f);
        drawCube();
        glPopMatrix();

        glPushMatrix();
        glTranslatef(2.5f, 0.0f, 0.0f);
        glScalef(0.75f, 0.75f, 0.75f);
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
        // Configurar la perspectiva de la cámara
        float ymax, xmax;
        ymax = (float) (zNear * Math.tan(Math.toRadians(fov / 2)));
        xmax = ymax * aspectRatio;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
    }
}//FINAL CLASS
