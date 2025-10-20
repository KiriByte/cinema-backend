FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY common common
COPY auth-service auth-service

RUN echo "rootProject.name = 'cinema-backend'" > settings.gradle
RUN echo "include 'common'" >> settings.gradle
RUN echo "include 'auth-service'" >> settings.gradle

RUN chmod +x ./gradlew
RUN ./gradlew :common:publishToMavenLocal
RUN ./gradlew :auth-service:clean :auth-service:build -x test

FROM eclipse-temurin:21-jre AS final

WORKDIR /app

COPY --from=build /app/auth-service/build/libs/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]