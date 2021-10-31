FROM openjdk:12-alpine

VOLUME /tmp

RUN mkdir common

WORKDIR common

COPY build/libs/message-consumer-*.jar app.jar

CMD [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]