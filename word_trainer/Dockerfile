FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY ./target/*.jar /app/application.jar

ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]
