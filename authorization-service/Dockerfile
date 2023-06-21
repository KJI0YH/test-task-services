FROM openjdk:8-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN ./gradlew --no-daemon dependencies
COPY src/ src/
RUN ./gradlew --no-daemon build

FROM openjdk:8-jre-alpine
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app/authorization.jar"]
COPY --from=builder /app/build/libs/authorization-*.jar authorization.jar