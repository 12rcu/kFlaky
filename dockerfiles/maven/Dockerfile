FROM maven:3.8.4-openjdk-11-slim
WORKDIR /app
COPY ../../build/libs/kFlaky-0.2.jar /app/kFlaky.jar
CMD ["java", "-jar", "kFlaky.jar"]