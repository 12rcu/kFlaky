FROM maven:3.8.4-openjdk-11
WORKDIR /app
COPY ./kFlaky-0.2.jar /app/kFlaky.jar
RUN git clone https://github.com/j256/ormlite-core.git
COPY ./config.json /app/config.json
COPY ./default.db /app/default.db
CMD ["java", "-jar", "kFlaky.jar"]