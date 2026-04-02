# Multi-stage build: compile the Spring Boot app, then run it on a slim JRE image.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies first for faster incremental builds.
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Copy source and package application.
COPY src ./src
RUN mvn -q -DskipTests clean package

# Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy executable Spring Boot jar from build stage.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
