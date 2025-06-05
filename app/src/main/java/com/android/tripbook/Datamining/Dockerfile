FROM gradle:8.7.0-jdk17-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN gradle --no-daemon dependencies

COPY src ./src

RUN gradle --no-daemon shadowJar -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 6000
ENTRYPOINT ["java", "-jar", "app.jar"]



