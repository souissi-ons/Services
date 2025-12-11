FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src/main/resources/notification.proto src/main/resources/
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090


ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-global:5432/notification_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=ons

ENTRYPOINT ["java", "-jar", "app.jar"]