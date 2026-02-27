FROM maven:3.8.3-openjdk-17-slim AS builder
LABEL maintainer="maheshkadambala18@gmail.com"

VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=./target/linkbharat-backend.jar

# Add the application's jar to the container
ADD ${JAR_FILE} linkbharat-backend.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/linkbharat-backend.jar"]
