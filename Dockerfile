# =================================================================
# ETAPA 1: BUILD
# Usa una imagen completa con JDK y Gradle para compilar el proyecto.
# 'jammy' (Ubuntu 22.04 LTS) proporciona una base robusta y compatible.
# =================================================================
FROM gradle:8-jdk17-jammy AS builder

# Establece el directorio de trabajo
WORKDIR /home/gradle/src

# Copia todo el código fuente, asignando la propiedad al usuario no-root 'gradle'
# para mejorar la seguridad durante la compilación.
COPY --chown=gradle:gradle . .

# Cambia al usuario 'gradle' para ejecutar la compilación.
USER gradle

# Ejecuta la compilación usando el Gradle Wrapper para asegurar la consistencia.
# --no-daemon es crucial para entornos de CI/CD y Docker.
RUN ./gradlew build --no-daemon

# =================================================================
# ETAPA 2: RUNTIME
# Usa una imagen JRE, que es más pequeña y segura
# que un JDK completo, ya que no incluye herramientas de compilación.
# =================================================================
FROM eclipse-temurin:17-jre-jammy

# Crea un grupo y un usuario no-root dedicados para ejecutar la aplicación.
# Este es un principio de seguridad fundamental (mínimo privilegio).
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 appuser && \
    mkdir /app && chown appuser:appuser /app

# Cambia al nuevo usuario no privilegiado.
USER appuser:appuser

# Establece el directorio de trabajo para la ejecución.
WORKDIR /app

# Copia únicamente el JAR compilado desde la etapa 'builder'.
# Asigna la propiedad del JAR al usuario 'appuser'.
COPY --from=builder --chown=appuser:appuser /home/gradle/src/build/libs/*.jar app.jar

# Expone el puerto en el que la aplicación escuchará.
EXPOSE 8080

# Comando de entrada para ejecutar la aplicación.
# Incluye optimizaciones de JVM cruciales para entornos de contenedores.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]