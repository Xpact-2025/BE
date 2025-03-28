FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/xpact-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /xpact.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "/xpact.jar"]