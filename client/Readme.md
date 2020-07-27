# Automation for NewBank client:


## Build the application

I have created a dockerfile that builds a container to create the jar and copies the .jar outside, renaming it

* In order to build the container with the app:

`docker build -t gabrielbcn/newbankclient:1.0 .`

* And to copy the file outside:
  
```bash
docker create -it --name temp gabrielbcn/newbankclient:1.0 sh
docker cp temp:/target/newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar .
mv newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar newbankclient.jar
```

## Automate the building of the client

* The client gets built with the following automation, which allows an artifact to be downloaded from the actions tab

```yaml
on:
  push:
    branches:
      - master
    paths-ignore:
      - "server/**"
      - "client/target/**"
      - "client/test/**"
      - "README.md"
      - "client/README.md"
      - ".gitignore"
      - ".github/workflows/server.yml"
      - ".github/workflows/clientrelease.yml"

name: Build-Client-Offer-Artifact

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    steps:
      # checkout the repo
      - name: "Checkout GitHub Action"
        uses: actions/checkout@v2

      - run: |
          cd client
          docker build -t gabrielbcn/newbankclient:1.0 .
          docker create -it --name temp gabrielbcn/newbankclient:1.0 sh
          docker cp temp:/target/newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar .
          mv newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar newbankclient.jar
          pwd
          ls -l

      - uses: actions/upload-artifact@v2
        with:
          path: /home/runner/work/NewBank/NewBank/client/newbankclient.jar
```


## Release the .jar

* The next step is to add it to the site release, with the following automation

```yaml
on: 
  release: 
    types: [created]

name: Build-Client-add-to-Release

jobs:
  build-and-add-to-release:
    runs-on: ubuntu-latest
    steps:
      # checkout the repo
      - name: "Checkout GitHub Action"
        uses: actions/checkout@v2

      - run: |
          cd client
          docker build -t gabrielbcn/newbankclient:1.0 .
          docker create -it --name temp gabrielbcn/newbankclient:1.0 sh
          docker cp temp:/target/newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar .
          mv newbankclient-1.0-SNAPSHOT-jar-with-dependencies.jar newbankclient.jar
          pwd
          ls -l
          
      - uses: actions/upload-artifact@v2
        with:
          path: newbankclient.jar

      - name: Upload to release
        uses: JasonEtco/upload-to-release@master
        with:
          args: client/newbankclient.jar application/java-archive
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
