FROM openjdk:17-jdk-slim

WORKDIR /home/planhattan/planhattan-api

COPY target/COMP47360_Team8_Backend-0.0.1-SNAPSHOT.jar ./planhattan-api.jar

# need to set database

CMD ["java", "-jar", "planhattan-api.jar"]