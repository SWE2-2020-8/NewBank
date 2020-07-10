# Automation for NewBank server:




## Build and run the server locally

I have created a dockerfile that builds a container to create the jar, then copies the jar to a new container and then has an entry point for the bank server

* In order to build the container:

`docker build -t gabrielbcn/newbankserver:1.0 .`

* And to make it run:

`docker run -d -p 80:80 gabrielbcn/newbankserver:1.0`

Obviously tags are flexible, here I was using my dockerhub account...

Also I have changed the port 80 to make it easier using the HTTP port...

* To interact with the container:

`socat - TCP4:localhost:80`



## Run in the cloud (Azure)

* I also have used an azure subscription of mine and followed https://docs.microsoft.com/en-us/azure/container-instances/container-instances-github-action

➜  NewBank git:(master) `az account set --subscription "Testing subscription"`

```json
{
    "cloudName": "AzureCloud",
    "homeTenantId": "baa3c6f4-b5ca-4d53-bca8-493933af40d8",
    "id": "0b046284-5d2d-4dd0-81eb-5ffdc294b771",
    "isDefault": false,
    "managedByTenants": [],
    "name": "Pay-as-you-go",
    "state": "Enabled",
    "tenantId": "baa3c6f4-b5ca-4d53-bca8-493933af40d8",
    "user": {
      "name": "gabriel.mesquida@me.com",
      "type": "user"
    }
}
```

* And created a resource group, named **gabriel2020swe8**:

➜  NewBank git:(master) `az group create --name gabriel2020swe8 --location southeastasia` 

```json
{
  "id": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8",
  "location": "southeastasia",
  "managedBy": null,
  "name": "gabriel2020swe8",
  "properties": {
    "provisioningState": "Succeeded"
  },
  "tags": null,
  "type": "Microsoft.Resources/resourceGroups"
}
```

* Created a new container registry in Azure, named **CRgabriel2020swe**:

➜  NewBank git:(master) `az acr create --resource-group gabriel2020swe8 --name CRgabriel2020swe --sku Basic`

```json
{- Finished ..
  "adminUserEnabled": false,
  "creationDate": "2020-07-08T03:09:28.524756+00:00",
  "dataEndpointEnabled": false,
  "dataEndpointHostNames": [],
  "encryption": {
    "keyVaultProperties": null,
    "status": "disabled"
  },
  "id": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.ContainerRegistry/registries/CRgabriel2020swe",
  "identity": null,
  "location": "southeastasia",
  "loginServer": "crgabriel2020swe.azurecr.io",
  "name": "CRgabriel2020swe",
  "networkRuleSet": null,
  "policies": {
    "quarantinePolicy": {
      "status": "disabled"
    },
    "retentionPolicy": {
      "days": 7,
      "lastUpdatedTime": "2020-07-08T03:09:30.309474+00:00",
      "status": "disabled"
    },
    "trustPolicy": {
      "status": "disabled",
      "type": "Notary"
    }
  },
  "privateEndpointConnections": [],
  "provisioningState": "Succeeded",
  "publicNetworkAccess": "Enabled",
  "resourceGroup": "gabriel2020swe8",
  "sku": {
    "name": "Basic",
    "tier": "Basic"
  },
  "status": null,
  "storageAccount": null,
  "tags": {},
  "type": "Microsoft.ContainerRegistry/registries"
}
```

* Then a service principal

➜  NewBank git:(master) `az ad sp create-for-rbac --scope "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8" --role Contributor --sdk-auth`

```json
Creating a role assignment under the scope of "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8"
{
  "clientId": "87bad605-1473-438b-bf15-bbcce373e24f",
  "clientSecret": **REMOVED**,
  "subscriptionId": "c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c",
  "tenantId": "60863f22-e0fd-4fa1-bc8c-87984d2c906b",
  "activeDirectoryEndpointUrl": "https://login.microsoftonline.com",
  "resourceManagerEndpointUrl": "https://management.azure.com/",
  "activeDirectoryGraphResourceId": "https://graph.windows.net/",
  "sqlManagementEndpointUrl": "https://management.core.windows.net:8443/",
  "galleryEndpointUrl": "https://gallery.azure.com/",
  "managementEndpointUrl": "https://management.core.windows.net/"
}
```

* I have created the following secrets in Github:
`AZURE_CREDENTIALS`
`AZURE_SP_ID`
`AZURE_SP_SECRET`

* Then I have given registry permissions to the service principal:

➜  NewBank git:(master) `az role assignment create --assignee "87bad605-1473-438b-bf15-bbcce373e24f" --scope "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.ContainerRegistry/registries/CRgabriel2020swe" --role AcrPush`

```json
{
  "canDelegate": null,
  "id": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.ContainerRegistry/registries/CRgabriel2020swe/providers/Microsoft.Authorization/roleAssignments/bc4a69eb-db78-432b-8571-1f0eac242e94",
  "name": "bc4a69eb-db78-432b-8571-1f0eac242e94",
  "principalId": "dd868f33-9eb1-4211-90fb-f4660b7459ae",
  "principalType": "ServicePrincipal",
  "resourceGroup": "gabriel2020swe8",
  "roleDefinitionId": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/providers/Microsoft.Authorization/roleDefinitions/8311e382-0749-4cb8-b61a-304f252e45ec",
  "scope": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.ContainerRegistry/registries/CRgabriel2020swe",
  "type": "Microsoft.Authorization/roleAssignments"
}
```

* And added more secrets:
`REGISTRY_LOGIN_SERVER`
`REGISTRY_USERNAME`
`REGISTRY_PASSWORD`
`RESOURCE_GROUP`

* And needed to register with the ContainerInstance namespace for Azure:

➜  NewBank git:(master) ✗ `az provider register --namespace Microsoft.ContainerInstance`

to check: `az provider show -n Microsoft.ContainerInstance`


* Creating a storage account with a file share

To keep proper defaults `az configure --defaults group=gabriel2020swe8 location=southeastasia`

Now creating the storage account:
➜  NewBank git:(master) `az storage account create --name gabriel2020swe8 --sku Standard_LRS`

```json
{- Finished ..
  "accessTier": "Hot",
  "azureFilesIdentityBasedAuthentication": null,
  "blobRestoreStatus": null,
  "creationTime": "2020-07-10T05:16:52.060482+00:00",
  "customDomain": null,
  "enableHttpsTrafficOnly": true,
  "encryption": {
    "keySource": "Microsoft.Storage",
    "keyVaultProperties": null,
    "requireInfrastructureEncryption": null,
    "services": {
      "blob": {
        "enabled": true,
        "keyType": "Account",
        "lastEnabledTime": "2020-07-10T05:16:52.107363+00:00"
      },
      "file": {
        "enabled": true,
        "keyType": "Account",
        "lastEnabledTime": "2020-07-10T05:16:52.107363+00:00"
      },
      "queue": null,
      "table": null
    }
  },
  "failoverInProgress": null,
  "geoReplicationStats": null,
  "id": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.Storage/storageAccounts/gabriel2020swe8",
  "identity": null,
  "isHnsEnabled": null,
  "kind": "StorageV2",
  "largeFileSharesState": null,
  "lastGeoFailoverTime": null,
  "location": "southeastasia",
  "name": "gabriel2020swe8",
  "networkRuleSet": {
    "bypass": "AzureServices",
    "defaultAction": "Allow",
    "ipRules": [],
    "virtualNetworkRules": []
  },
  "primaryEndpoints": {
    "blob": "https://gabriel2020swe8.blob.core.windows.net/",
    "dfs": "https://gabriel2020swe8.dfs.core.windows.net/",
    "file": "https://gabriel2020swe8.file.core.windows.net/",
    "internetEndpoints": null,
    "microsoftEndpoints": null,
    "queue": "https://gabriel2020swe8.queue.core.windows.net/",
    "table": "https://gabriel2020swe8.table.core.windows.net/",
    "web": "https://gabriel2020swe8.z23.web.core.windows.net/"
  },
  "primaryLocation": "southeastasia",
  "privateEndpointConnections": [],
  "provisioningState": "Succeeded",
  "resourceGroup": "gabriel2020swe8",
  "routingPreference": null,
  "secondaryEndpoints": null,
  "secondaryLocation": null,
  "sku": {
    "name": "Standard_LRS",
    "tier": "Standard"
  },
  "statusOfPrimary": "available",
  "statusOfSecondary": null,
  "tags": {},
  "type": "Microsoft.Storage/storageAccounts"
}
```

And finally creating the file share:
➜  NewBank git:(master) ✗ `az storage share create --name serverstorage  --account-name "gabriel2020swe8"`
There is no credential provided in your command and environment, we will query account key for your storage account.
Please provide --connection-string, --account-key or --sas-token as credential, or use `--auth-mode login` if you have required RBAC roles in your command. For more information about RBAC roles in stoarge, you can see https://docs.microsoft.com/en-us/azure/storage/common/storage-auth-aad-rbac-cli.
Setting corresponding environment variable can avoid inputting credential in your command. Please use --help to get more information.
```json
{
  "created": true
}
```

And get the key:
`az storage account keys list --resource-group gabriel2020swe8 --account-name gabriel2020swe8 --query "[0].value" --output tsv`

Keys stored in secrets:
`AZ_STORAGE_ACCOUNT_NAME`
`AZ_STORAGE_ACCOUNT_KEY`
`AZ_STORAGE_SHARE_NAME`


* And finally the code: (NewBank/.github/workflows/main.yml)

```yaml
on: [push]
name: Linux_Container_Workflow

jobs:
    build-and-deploy:
        runs-on: ubuntu-latest
        steps:
        # checkout the repo
        - name: 'Checkout GitHub Action'
          uses: actions/checkout@master
          
        - name: 'Login via Azure CLI'
          uses: azure/login@v1
          with:
            creds: ${{ secrets.AZURE_CREDENTIALS }}
        
        - name: 'Build and push image'
          uses: azure/docker-login@v1
          with:
            login-server: ${{ secrets.REGISTRY_LOGIN_SERVER }}
            username: ${{ secrets.REGISTRY_USERNAME }}
            password: ${{ secrets.REGISTRY_PASSWORD }}
        - run: |
            cd server
            docker build . -t ${{ secrets.REGISTRY_LOGIN_SERVER }}/bankserver:${{ github.sha }}
            docker push ${{ secrets.REGISTRY_LOGIN_SERVER }}/bankserver:${{ github.sha }}
        - name: 'Deploy to Azure Container Instances'
          uses: 'azure/aci-deploy@v1'
          with:
            resource-group: ${{ secrets.RESOURCE_GROUP }}
            dns-name-label: ${{ github.repository_owner }}
            image: ${{ secrets.REGISTRY_LOGIN_SERVER }}/bankserver:${{ github.sha }}
            registry-login-server: ${{ secrets.REGISTRY_LOGIN_SERVER }}
            registry-username: ${{ secrets.REGISTRY_USERNAME }}
            registry-password: ${{ secrets.REGISTRY_PASSWORD }}
            name: bankserver
            azure-file-volume-share-name: ${{ AZ_STORAGE_SHARE_NAME }}
            azure-file-volume-account-name: ${{ secrets.AZ_STORAGE_ACCOUNT_NAME }}
            azure-file-volume-account-key: ${{ secrets.AZ_STORAGE_ACCOUNT_KEY }}
            azure-file-volume-mount-path: /mnt/volume
            location: 'southeastasia'
```

* And if it all goes well:

➜  NewBank git:(master) `az container show --resource-group gabriel2020swe8 --name bankserver --query "{FQDN:ipAddress.fqdn,ProvisioningState:provisioningState}" --output table`

```
FQDN                                               ProvisioningState
-------------------------------------------------  -------------------
gabriel2020swe814.southeastasia.azurecontainer.io  Succeeded
```

* Interact with `socat - TCP4:swe2-2020-8.southeastasia.azurecontainer.io:80` **DONE**
  
* Or with the browser at http://swe2-2020-8.southeastasia.azurecontainer.io

* In order to check the status of the ACIs

➜  server git:(master) `az container list`

```json

[
  {
    "containers": [
      {
        "command": [],
        "environmentVariables": [],
        "image": "crgabriel2020swe.azurecr.io/bankserver:8ea7156a0ff02667ad8e27536b232568f2935d23",
        "instanceView": null,
        "livenessProbe": null,
        "name": "bankserver",
        "ports": [
          {
            "port": 80,
            "protocol": "TCP"
          }
        ],
        "readinessProbe": null,
        "resources": {
          "limits": null,
          "requests": {
            "cpu": 1.0,
            "gpu": null,
            "memoryInGb": 1.5
          }
        },
        "volumeMounts": null
      }
    ],
    "diagnostics": null,
    "dnsConfig": null,
    "id": "/subscriptions/c4c4991b-008d-40f4-ab0e-52bdf0dfbf9c/resourceGroups/gabriel2020swe8/providers/Microsoft.ContainerInstance/containerGroups/bankserver",
    "identity": null,
    "imageRegistryCredentials": [
      {
        "password": null,
        "server": "crgabriel2020swe.azurecr.io",
        "username": "87bad605-1473-438b-bf15-bbcce373e24f"
      }
    ],
    "instanceView": null,
    "ipAddress": {
      "dnsNameLabel": "SWE2-2020-8",
      "fqdn": "SWE2-2020-8.southeastasia.azurecontainer.io",
      "ip": "20.44.209.147",
      "ports": [
        {
          "port": 80,
          "protocol": "TCP"
        }
      ],
      "type": "Public"
    },
    "location": "southeastasia",
    "name": "bankserver",
    "networkProfile": null,
    "osType": "Linux",
    "provisioningState": "Succeeded",
    "resourceGroup": "gabriel2020swe8",
    "restartPolicy": "Always",
    "tags": null,
    "type": "Microsoft.ContainerInstance/containerGroups",
    "volumes": null
  }
]

```

and another way to interact with them:

`az container exec --container-name bankserver --exec-command sh --name bankserver`




## My first approach, pushing the container to dockerhub

Been creating the container and uploading to dockerhub with the secrets:

`DOCKER_USERNAME`
`DOCKER_PASSWORD`

And the code: (NewBank/.github/workflows/main.yml)

```yaml
# Creating a bank server container

name: CreateBankServerContainer

# Controls when the action will run. Triggers the workflow on push or pull request
# events but only for the master branch
on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

# Different jobs for a workflow
jobs:
  # We only define build for now
  build:
    # The type of runner that the job will run on
    runs-on: ubuntu-latest
    # Steps that will be done
    steps:

      # Checks-out your repository under $GITHUB_WORKSPACE, so your job can access it
      - uses: actions/checkout@master
          
      # Pushing to dockerhub    
      - name: Publish container to registry
        uses: elgohr/Publish-Docker-Github-Action@master
        with:
          name: gabrielbcn/newbankserver:1.0
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          workdir: server

      # Build is done, container is in place
```

It works, but I ended up using the Azure container repo