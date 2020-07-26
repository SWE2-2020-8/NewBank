# Automation for NewBank client:




## Build the application

I have created a dockerfile that builds a container to create the jar and copies the .jar outside, renaming it

* In order to build the container to achieve local output:

`docker build -t gabrielbcn/newbankclient:1.0 .`



To interact with it

`docker run -it gabrielbcn/newbankclient:1.0`