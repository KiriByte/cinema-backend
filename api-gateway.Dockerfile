FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY common common
COPY api-gateway api-gateway

RUN echo "rootProject.name = 'cinema-backend'" > settings.gradle
RUN echo "include 'common'" >> settings.gradle
RUN echo "include 'api-gateway'" >> settings.gradle

RUN chmod +x ./gradlew
RUN ./gradlew :common:publishToMavenLocal
RUN ./gradlew :api-gateway:clean :api-gateway:build -x test

FROM eclipse-temurin:21-jre AS final

WORKDIR /app

COPY --from=build /app/api-gateway/build/libs/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]