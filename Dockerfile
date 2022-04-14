# DockerFile to create ProductService image
FROM gradle:7-jdk-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -p ProductService
FROM openjdk:11-jre-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/ProductService/build/libs/*.jar /app/spring-boot-application.jar
RUN apt-get update && apt-get install -y dumb-init \
  && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java","-jar","/app/spring-boot-application.jar"]

