FROM openjdk:11-jre-slim-buster
COPY target/*.jar payment-app.jar
ENTRYPOINT ["java","-jar","/payment-app.jar"]