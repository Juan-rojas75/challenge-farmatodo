# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY target/challenge-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","app.jar"]

# Etapa 1: build
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Etapa 2: deploy
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/challenge-0.0.1-SNAPSHOT.jar app.jar

# Perfil prod por defecto
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
