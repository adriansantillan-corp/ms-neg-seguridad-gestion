# Etapa 1: Construcción con JDK 17.0.8+8 (Temurin oficial)
FROM eclipse-temurin:17.0.8_8-jdk-alpine AS build

# Trabajar dentro del contenedor
WORKDIR /home/gradle/src

# Copiar archivos con permisos correctos
COPY --chown=gradle:gradle . .

# Ejecutar build con Gradle Wrapper (mejor práctica)
RUN ./gradlew build --no-daemon

# Etapa 2: Ejecución con JRE ligero (misma versión)
FROM eclipse-temurin:17.0.8_8-jre-alpine

# Directorio de la app
WORKDIR /app

# Copiar el JAR desde la etapa de build
# Asumiendo que el JAR se genera en build/libs/ y tiene nombre como: myapp-1.0.jar
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Puerto que usa tu app (según application.yml)
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]