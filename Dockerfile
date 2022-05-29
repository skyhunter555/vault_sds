FROM openjdk:8-jdk-alpine

# The application's jar file
ARG JAR_FILE=rnd-vault-sds/build/libs/rnd-vault-sds-1.0.0.jar
ARG JAVA_OPTS="-Xmx256m -Xms256m"
# Add the application's jar to the container
ADD ${JAR_FILE} /app.jar

# HTTP port
EXPOSE 8008
EXPOSE 6556
EXPOSE 5005

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar