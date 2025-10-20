FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
#COPY gradle.properties .

RUN echo "rootProject.name = 'cinema-backend'" > settings.gradle
RUN echo "include 'common'" >> settings.gradle
RUN echo "include 'movie-service'" >> settings.gradle

COPY common common
COPY movie-service movie-service
RUN chmod +x ./gradlew
RUN ./gradlew :common:publishToMavenLocal
RUN ./gradlew :movie-service:clean :movie-service:build -x test

FROM eclipse-temurin:21-jre AS final

WORKDIR /app

COPY --from=build /app/movie-service/build/libs/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]