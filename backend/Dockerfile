FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests


FROM eclipse-temurin:17-jdk
COPY --from=build target/reservationsystem-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]