# Etapa 1: Construcción
FROM maven:3.8.7-openjdk-17 AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/reservationsystem-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]