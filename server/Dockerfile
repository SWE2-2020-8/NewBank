# First stage of the build
FROM maven:3.6.3-jdk-8 AS MAVEN_BUILD

# copy the source tree and the pom.xml to our new container
COPY . .

# package our application code
RUN mvn clean package

# Second stage of the build with open jre 8 on alpine 3.9
FROM openjdk:8-jre-alpine3.9

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/newbankserver-1.0-SNAPSHOT.jar /target/newbankserver-1.0-SNAPSHOT.jar

# set the startup command to execute the jar
CMD ["java", "-jar", "target/newbankserver-1.0-SNAPSHOT.jar"]

# port is in 14002