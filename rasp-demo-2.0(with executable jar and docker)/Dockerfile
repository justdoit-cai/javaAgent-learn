FROM dockette/jdk8

WORKDIR /app

COPY *.jar /app/

CMD ["java", "-jar", "-javaagent:myRasp.jar" ,"jackson-vul-environment.jar"]
