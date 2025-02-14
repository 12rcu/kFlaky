FROM gradle:8-jdk11-corretto AS kflaky
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM ubuntu:22.04
WORKDIR /app
RUN apt update
RUN apt install git openjdk-11-jdk maven -y
RUN export JAVA_HOME=/usr/lib/jvm/openjdk-11
RUN export MAVEN_OPTS="-Xmx4000m" # increase memory limit for maven executions

COPY --from=kflaky /app/build/libs/kFlaky-0.2.jar /app/kFlkay.jar

ENTRYPOINT ["java", "-Xmx4096M", "-jar", "kFlkay.jar"]