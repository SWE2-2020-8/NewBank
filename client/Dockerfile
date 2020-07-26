# First stage of the build
FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD

# copy the source tree and the pom.xml to our new container
COPY . .

# package our application code
RUN mvn clean package

# the artifact for local copy
FROM scratch AS artifact

# copy outside
COPY --from=MAVEN_BUILD /target/newbankserver-1.0-SNAPSHOT-jar-with-dependencies.jar .

