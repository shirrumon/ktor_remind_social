FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/ktor-get-started-sample-all.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]