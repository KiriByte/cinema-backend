FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY eureka-server eureka-server

RUN echo "rootProject.name = 'cinema-backend'" > settings.gradle
RUN echo "include 'eureka-server'" >> settings.gradle

RUN chmod +x ./gradlew
RUN ./gradlew :eureka-server:clean :eureka-server:build -x test

FROM eclipse-temurin:21-jre AS final

WORKDIR /app

COPY --from=build /app/eureka-server/build/libs/*.jar app.jar

EXPOSE 8761
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]