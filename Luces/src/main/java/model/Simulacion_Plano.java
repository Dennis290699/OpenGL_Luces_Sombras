package model;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHT1;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_LOCAL_VIEWER;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFrustum;
import static org.lwjgl.opengl.GL11.glLightModeli;
import static org.lwjgl.opengl.GL11.glLightfv;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
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

public class Simulacion_Plano {

    private long window;
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = -7.0f;
    private float cameraRotationX = 0.0f;
    private float cameraRotationY = 0.0f;

    public void run() {
        System.out.println("¡LWJGL " + Version.getVersion() + " funcionando!");

        try {
            init();
            loop();
        } finally {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo inicializar GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(800, 600, "Luces", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("No se pudo crear la ventana de GLFW");
        }

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

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float) 800 / 600;
        gluPerspective(45.0f, aspectRatio, 0.1f, 100.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        setupLighting();

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
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();
            glTranslatef(cameraX, cameraY, cameraZ);
            glRotatef(cameraRotationX, 1.0f, 0.0f, 0.0f);
            glRotatef(cameraRotationY, 0.0f, 1.0f, 0.0f);

            drawCubes();
            drawFloor(); // Dibujar el plano para las sombras

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void setupLighting() {
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);

        glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);

        // Luz ambiental
        float[] ambientLight = {0.3f, 0.3f, 0.3f, 1.0f};
        FloatBuffer ambientLightBuffer = BufferUtils.createFloatBuffer(4);
        ambientLightBuffer.put(ambientLight).flip();
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLightBuffer);

        // Luz direccional para simular el sol
        float[] lightDirection = {1.0f, 1.0f, 1.0f, 1.0f}; // Dirección de la luz (hacia abajo)
        FloatBuffer lightDirectionBuffer = BufferUtils.createFloatBuffer(4);
        lightDirectionBuffer.put(lightDirection).flip();
        glLightfv(GL_LIGHT1, GL_POSITION, lightDirectionBuffer);
        glLightfv(GL_LIGHT1, GL_DIFFUSE, ambientLightBuffer); // Color de la luz
        glEnable(GL_LIGHT1);
    }

    private void drawCubes() {
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
        glBegin(GL_QUADS);

        // Front face
        glColor3f(1.0f, 0.0f, 0.0f);
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

        glEnd();
    }

    private void drawFloor() {
        glBegin(GL_QUADS);
        glColor3f(0.2f, 0.2f, 0.2f); // Cambiar el color del suelo a un tono más oscuro
        glVertex3f(-10.0f, -2.0f, -10.0f);
        glVertex3f(-10.0f, -2.0f, 10.0f);
        glVertex3f(10.0f, -2.0f, 10.0f);
        glVertex3f(10.0f, -2.0f, -10.0f);
        glEnd();
    }
    
    private void gluPerspective(float fov, float aspectRatio, float zNear, float zFar) {
        float ymax, xmax;
        ymax = (float) (zNear * Math.tan(Math.toRadians(fov / 2)));
        xmax = ymax * aspectRatio;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
    }
}//FINAL CLASS
