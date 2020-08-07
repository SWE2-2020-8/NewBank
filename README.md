# SWE2-2020-8 - New Bank Project - Group 8

### Automation actions status
![Build-Server-and-Send-to-Azure](https://github.com/SWE2-2020-8/NewBank/workflows/Build-Server-and-Send-to-Azure/badge.svg)
![Build-Client-Offer-Artifact](https://github.com/SWE2-2020-8/NewBank/workflows/Build-Client-Offer-Artifact/badge.svg)
![Build-Client-add-to-Release](https://github.com/SWE2-2020-8/NewBank/workflows/Build-Client-add-to-Release/badge.svg)

### To jump quickly to...
- [Wiki](https://github.com/SWE2-2020-8/NewBank/wiki), for this document and others
- [Backlog, sprint backlog and weekly reports](https://github.com/SWE2-2020-8/NewBank/wiki/NewBank-backlog-and-sprints-page)
- [User Stories and Requirements](https://github.com/SWE2-2020-8/NewBank/wiki/User-Stories-and-Requirements)
- [Slack channel](https://join.slack.com/t/swe2-2020-8/shared_invite/zt-gj3iswzo-inC7jb3iaSAyQ1pEPR3jYQ) for discussions 
- [Main board](https://github.com/orgs/SWE2-2020-8/projects/1), kanban board for management activities
- [NewBank code board](https://github.com/SWE2-2020-8/NewBank/projects/1), kanban board for code works
- [The code repository](https://github.com/SWE2-2020-8/NewBank)
- [Issues](https://github.com/SWE2-2020-8/NewBank/issues), to identify activities including defects
- [Milestones](https://github.com/SWE2-2020-8/NewBank/milestones), to track weekly progress
- [Insights](https://github.com/SWE2-2020-8/NewBank/pulse), to see what's going on
- [Actions](https://github.com/SWE2-2020-8/NewBank/actions), for code automation (GitOps)
- [Releases](https://github.com/SWE2-2020-8/NewBank/releases/) to find the latest version of the code and a ready-to-run client

### Working software
* Server is running in `http://swe2-2020-8.southeastasia.azurecontainer.io`
* Interact directly with server using `socat - TCP4:swe2-2020-8.southeastasia.azurecontainer.io:80`
* Download the latest Java/JavaFX **newbankclient** in the [releases section](https://github.com/SWE2-2020-8/NewBank/releases)

### Automation notes
* In order to understand how to run the server locally or how the automation has been built to deploy the server to Azure click [here](https://github.com/SWE2-2020-8/NewBank/blob/master/server/Readme.md)
* And [here](https://github.com/SWE2-2020-8/NewBank/blob/master/client/Readme.md) to understand the automation for building and releasing the client: newbankclient.jar
