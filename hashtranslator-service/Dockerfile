FROM gradle:jdk8 AS builder
COPY . /app
WORKDIR /app
RUN gradle build

FROM openjdk:8-jre-alpine
COPY --from=builder /app/build/libs/hashtranslator-*.jar hashtranslator.jar
EXPOSE 8080
CMD ["java", "-jar", "hashtranslator.jar"]