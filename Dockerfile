# Multi-stage Dockerfile for Clinical Dose Management Platform
# Stage 1: Build & Package
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Cache dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Distroless / Hardened Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for FDA / 21 CFR Part 11 production compliance
RUN addgroup -S dmsgroup && adduser -S dmsuser -G dmsgroup

COPY --from=builder /workspace/target/*.jar app.jar

USER dmsuser

ENV SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:+UseStringDeduplication -Xms512m -Xmx2048m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
