FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ms-account.jar
ENTRYPOINT ["java","-jar","/ms-account.jar"]