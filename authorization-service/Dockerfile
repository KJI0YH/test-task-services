FROM gradle:jdk8 AS builder
COPY . /app
WORKDIR /app
RUN gradle build

FROM openjdk:8-jre-alpine
COPY --from=builder /app/build/libs/authorization-*.jar authorization.jar
EXPOSE 8080
CMD ["java", "-jar", "authorization.jar"]
