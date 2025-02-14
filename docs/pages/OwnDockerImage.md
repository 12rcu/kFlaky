# Bring your own Docker image

Base docker image

![Docker Image Version](https://img.shields.io/docker/v/12build/kflkay?logo=docker&label=12build%2Fkflaky)

## Create a own image

Use the image as a base and make sure that the entry point is still the kFlaky.jar.
The base directory is `/app`

Dockerfile
````dockerfile
FROM 12build/kFlaky:latest
WORKDIR /app
# ...
ENTRYPOINT ["java", "-jar", "kFlkay.jar"]
````

## Change the java version

````dockerfile
FROM 12build/kFlaky:latest
WORKDIR /app
RUN apt install openjdk-23-jdk -y
RUN export JAVA_HOME=/usr/lib/jvm/openjdk-23
# ...
ENTRYPOINT ["java", "-jar", "kFlkay.jar"]
````