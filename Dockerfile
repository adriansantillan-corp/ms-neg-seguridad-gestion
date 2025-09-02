# Etapa 1: Construcción con imagen oficial de Gradle (incluye JDK + usuario gradle)
FROM gradle:17-jdk-alpine AS build

# Trabajar dentro del contenedor
WORKDIR /home/gradle/src

# Copiar archivos con permisos correctos
COPY --chown=gradle:gradle . .

# Cambiar a usuario gradle si quieres construir sin root (opcional, pero seguro)
USER gradle

# Ejecutar build con Gradle Wrapper (mejor práctica)
RUN ./gradlew build --no-daemon

# Etapa 2: Ejecución con JRE ligero (misma versión)
FROM eclipse-temurin:17-jre-alpine

# Crear grupo y usuario no-root con UID/GID fijos (mejor para producción)
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Directorio de la app
WORKDIR /app

# Copiar el JAR y asignar propiedad al usuario no-root
COPY --from=build --chown=appuser:appuser /home/gradle/src/build/libs/*.jar app.jar

# Cambiar al usuario no-root
USER appuser:appuser

# Puerto que usa tu app (según application.yml)
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]