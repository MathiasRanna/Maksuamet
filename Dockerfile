FROM public.ecr.aws/amazoncorretto/amazoncorretto:21
CMD ["mdrir", "/tmp/csv_files"]
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]