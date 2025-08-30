# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY target/challenge-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","app.jar"]

# Etapa 1: Compilación de la aplicación
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Aquí está la solución: hacemos que el script mvnw sea ejecutable
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Etapa 2: Creación de la imagen de despliegue
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/challenge-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]