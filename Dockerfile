FROM openjdk:21

COPY target/Main.jar /usr/src/app/Main.jar
COPY target/ChatGpt.jar /usr/src/app/ChatGpt.jar

WORKDIR /usr/src/app

CMD ["sh", "-c", "java -jar ${APP_NAME}.jar"]

# По умолчанию запускается Main
ARG APP_NAME=Main
