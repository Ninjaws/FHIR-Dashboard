FROM maven:3.9.16-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:resolve dependency:resolve-plugins -B
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -Pproduction -DskipTests

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
RUN apk upgrade --no-cache && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup
COPY --from=build --chown=appuser:appgroup /app/target/fhir-dashboard.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
