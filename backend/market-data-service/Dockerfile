FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY market-data-service/target/market-data-service-1.0-SNAPSHOT.jar app.jar
USER app
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
