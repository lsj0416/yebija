FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ls -la && echo "--- gradle wrapper ---" && ls -la gradle/wrapper/ 2>/dev/null || echo "gradle/wrapper NOT FOUND"
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
RUN chmod +x gradlew && ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/yebija-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
