# Automation for NewBank client:


## Build the application

I have created a dockerfile that builds a container to create the jar and copies the .jar outside, renaming it

* In order to build the container with the app:

`docker build -t gabrielbcn/newbankclient:1.0 .`

* And to copy the file outside:
  
```
docker create -it --name temp gabrielbcn/newbankclient:1.0 sh

docker cp temp:/target/newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar .

mv newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar newbankclient.jar
```

## Release the .jar

* The next step is to add it to the site release:

