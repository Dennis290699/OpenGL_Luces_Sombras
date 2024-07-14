# OpenGL_Luces_Sombras

## Descripción
Este proyecto es una simulación de iluminación utilizando OpenGL en Java. Está configurado con Gradle y puede ser importado y ejecutado en Eclipse.

## Requisitos Previos
- Eclipse IDE para Java Developers.
- Java Development Kit (JDK) instalado.
- Gradle instalado o utilizar el Gradle Wrapper incluido en el proyecto.

## Importar el Proyecto en Eclipse
1. **Abrir Eclipse** y seleccionar el espacio de trabajo deseado.
2. **Importar el Proyecto Gradle:**
   - Navegar a `File > Import`.
   - Seleccionar `Existing Gradle Project` en la lista de opciones y hacer clic en `Next`.
   - Hacer clic en `Browse` y seleccionar la carpeta raíz del proyecto (`OpenGL_Luces_Sombras`).
   - Hacer clic en `Finish` para completar la importación.

## Configuración de Gradle
Tu proyecto utiliza Gradle para la gestión de dependencias y la construcción. Aquí hay un ejemplo básico de cómo podría verse tu archivo `build.gradle.kts`:

```kotlin
plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
	implementation("org.lwjgl:lwjgl:3.3.3")
	implementation("org.lwjgl:lwjgl-opengl:3.3.3")
	implementation("org.lwjgl:lwjgl-glfw:3.3.3")
	implementation("org.joml:joml:1.9.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

## Configuración del Entorno de Ejecución
Para ejecutar tu proyecto:
1. **Seleccionar el archivo principal**: Navegar al archivo `Main.java` en `src/main/java/principal`.
2. **Clic derecho en `Main.java`**: Seleccionar `Run As > Java Application`.

## Resolución de Problemas Comunes
- **Errores de Compilación**: Asegúrate de que todas las dependencias están correctamente especificadas en tu `build.gradle.kts`.
- **Problemas de Ejecución**: Verifica que los archivos DLL están correctamente ubicados en `src/main/resources/ogl`.

## Uso del Gradle Wrapper
Para construir tu proyecto sin necesidad de tener Gradle instalado en tu sistema:
- En Unix/Linux/MacOS: `./gradlew build`
- En Windows: `gradlew.bat build`
