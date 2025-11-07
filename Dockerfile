# Use official Java 21 image (lightweight)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy jar file into container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose backend port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
