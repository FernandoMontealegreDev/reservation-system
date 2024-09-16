# Usa una imagen de Maven oficial para construir el proyecto
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo de configuración de Maven
COPY pom.xml .

# Descarga las dependencias definidas en el pom.xml
RUN mvn dependency:go-offline

# Copia el código fuente del proyecto
COPY src ./src

# Construye el proyecto con Maven
RUN mvn clean package -DskipTests

# Usa una imagen de Java oficial para ejecutar la aplicación
FROM eclipse-temurin:17-jdk-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR construido desde la fase de construcción
COPY --from=build /app/target/reservationsystem-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Variable de entorno para pasar el perfil activo
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]