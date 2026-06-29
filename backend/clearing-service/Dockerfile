FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY clearing-service/target/clearing-service-1.0-SNAPSHOT.jar app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
