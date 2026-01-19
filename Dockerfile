# ---- Build Stage ----
FROM maven:3.9.12-eclipse-temurin-25 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Render expects the app to bind to PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
