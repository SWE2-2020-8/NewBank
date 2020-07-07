I have created a dockerfile that builds a container to create the jar, then copies the jar to a new container and then has an entry point for the bank server

In order to build the container

docker build -t gabrielbcn/newbankserver:1.0 .

And testing that the container works

docker run -d -p 8080:14002 gabrielbcn/newbankserver:1.0

