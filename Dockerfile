FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/xpact-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /xpact.jar

ENTRYPOINT ["java",
    "-Dspring.profiles.active=dev",
    "-Duser.timezone=Asia/Seoul",
    "-Dcom.sun.management.jmxremote=true",
    "-Dcom.sun.management.jmxremote.local.only=false",
    "-Dcom.sun.management.jmxremote.port=1099",
    "-Dcom.sun.management.jmxremote.ssl=false",
    "-Djava.rmi.server.hostname=3.39.122.216",
    "-Dcom.sun.management.jmxremote.rmi.port=1099",
    "-Dcom.sun.management.jmxremote.authenticate=false",
    "-jar",
    "/xpact.jar"]