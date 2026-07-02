# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
ARG SPRING_PROFILES_ACTIVE=dev
WORKDIR /app
COPY --from=build /app/target/notification-service-*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENTRYPOINT ["java", "-jar", "app.jar"]
