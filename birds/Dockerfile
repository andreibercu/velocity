FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /app

COPY target/birds-*.jar app.jar
#COPY target/birds-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
