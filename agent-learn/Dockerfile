FROM openjdk:8

RUN useradd web
USER web
WORKDIR /app
COPY *.jar /app/

CMD ["java", "-jar","app.jar"]
