# What is this

This is a Java Azure Function that is triggered by an SQLTrigger on an Azure SQL Database. It then sends an email.

It demonstrates how to build SQLTrigger App functions and then also how to use the graphAPI to send emails

To get this working, locally you'll need a local.settings.json file with the appropriate settings.

{
  "IsEncrypted": false,
  "Values": {
    "AZURE_T_ID": "",
    "AZURE_CLIENT_ID": "",
    "AZURE_CLIENT_SECRET": "",
    "GRAPH_FROM_USER_ID": "emailtosendfrom",
    "NOTIFICATION_EMAIL": "emailtosendto",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "SqlConnectionString": "connectionstringtoconnecttoDB;"
  }
}

when it's deployed, these settings should be in Environement Variables (except for AzureWebJobsStorage, that should be prepopulated to your App Func's storage acc).

This solution doesn't seem to work for Flex Consumption for some reason, but it works for App Service Plan hosted ones.



# Setting up Database

Your database will need to have network line of sight to the Azure function, the right permissions and have change tracking turned on.

Network line of sight is specific to your solution so I won't delve into it.


## Permissions
In terms of permissions, locally, it'll use AzureDefaultCredentials, so whoever is logged into your Azure CLI will need access. In deployed state, the managed ID of the app function will need access.

To grant access run the following commands.

```
CREATE USER [appfuncname] FROM EXTERNAL PROVIDER;
ALTER ROLE db_datareader ADD MEMBER [appfuncname];
ALTER ROLE db_datawriter ADD MEMBER [appfuncname];
ALTER ROLE db_owner ADD MEMBER [appfuncname];
```

It actually needs db_owner at least at the start because it needs to create some db tables.

## Change tracking

You also need to run the below on the table you want, to get SQLTrigger to trigger

```
ALTER DATABASE [db3]
SET CHANGE_TRACKING = ON
(CHANGE_RETENTION = 2 DAYS, AUTO_CLEANUP = ON);


ALTER TABLE [dbo].[Employees]  
ENABLE CHANGE_TRACKING  
WITH (TRACK_COLUMNS_UPDATED = ON) 
```


# App Registration

The app function needs an app registration for it to send emails thru the Microsoft Graph API. You can create a new app registration in the Azure portal.

The app registration will also need permissions from Exchange Online's RBAC for App system.

Look at the ExchangeOnline.ps1 file for how to do this, but essentially, you need to create a SP inside Exchange Online linked to the app registration. Then set up a scope (eg. a mailbox it can send from) and assign it to the Service Principal.

