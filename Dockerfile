### Добавление jar микросервиса
WORKDIR /
COPY target/*.jar app.jar

### Команда запуска приложения
CMD java $JAVA_OPTS -jar app.jar -Dspring.config.location=/config/

#BUILD
mvn clean install

#RUN
java -jar autoscaler-1.0.0.jar
