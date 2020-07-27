#
# Dockerfile to build the server
#
# Manually:
#
# docker build --build-arg db_master_key=DB_MASTER_KEY -t gabrielbcn/newbankserver:1.0 .
# docker run -d -p 80:80 gabrielbcn/newbankserver:1.0
#
# Github automation:
#
# docker build --build-arg db_master_key=${{ secrets.DB_MASTER_KEY }} -t ${{ secrets.REGISTRY_LOGIN_SERVER }}/bankserver:${{ github.sha }} .
#
# later push into registry...
# docker push ${{ secrets.REGISTRY_LOGIN_SERVER }}/bankserver:${{ github.sha }}

# First stage of the build
FROM maven:3.6.3-jdk-8 AS MAVEN_BUILD

# copy the source tree (including pom.xml) to our new container
COPY . .

# package our application code
RUN mvn clean package

# Second stage of the build with open jre 8 on alpine 3.9
FROM openjdk:8-jre-alpine3.9

# to pass the MASTER_KEY to the environment of the running container
ARG db_master_key

# Key in environment
ENV DB_MASTER_KEY=${db_master_key}

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /target/newbankserver-1.0-SNAPSHOT-jar-with-dependencies.jar /target/newbankserver.jar

# set the startup command to execute the jar
CMD ["java", "-jar", "target/newbankserver.jar"]

# port is in 14002 -> moved to 80