FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/soap-archives-service-1.0.0.jar app.jar
EXPOSE 8081

ENV DB_URL=jdbc:postgresql://postgres-global:5432/archives_db
ENV DB_USER=postgres
ENV DB_PASSWORD=password123

ENTRYPOINT ["java", "-jar", "app.jar"]