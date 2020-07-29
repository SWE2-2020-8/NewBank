# First stage of the build
FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD

# RUN yum install openjfx 


# copy the source tree and the pom.xml to our new container
COPY . .

# package our application code
RUN mvn clean package


# build
# docker build -t gabrielbcn/newbankclient:1.0 .
# copy outside
# docker run --rm --entrypoint cat gabrielbcn/newbankclient:1.0  /target/newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar > newbankclient.jar

