# DockerFile to create ProductService image
FROM gradle:7-jdk-alpine AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
ENTRYPOINT ["gradle", "--"]
