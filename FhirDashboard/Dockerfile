FROM maven:3.9.16-amazoncorretto-25-debian-trixie AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -Pproduction -DskipTests

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
RUN apk update && apk upgrade --no-cache
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
