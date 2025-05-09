# Use OpenJDK image from Docker Hub
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot jar into the container
COPY target/uvaXplore-0.0.1-SNAPSHOT.jar uvaXplore-0.0.1-SNAPSHOT.jar

# Expose the port Spring Boot runs on (default 8080)
EXPOSE 8080

# Set the entry point for the container
CMD ["java", "-jar", "spring-app.jar"]
