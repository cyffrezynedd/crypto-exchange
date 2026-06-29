FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY trading-service/target/trading-service-1.0-SNAPSHOT.jar app.jar
USER app
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
