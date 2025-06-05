# Stage 1: Build
FROM gradle:8.7.0-jdk17-alpine AS builder
WORKDIR /app

# Copy gradle files first to leverage cache
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Download dependencies (this will cache them if they're not changed)
RUN gradle --no-daemon dependencies

# Copy source code
COPY src ./src

# Build the application
RUN gradle --no-daemon shadowJar -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar
EXPOSE 6000
ENTRYPOINT ["java", "-jar", "app.jar"]



