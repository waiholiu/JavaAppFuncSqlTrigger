# Configuration - UPDATE THESE VALUES

# find this in entra ID
$AppRegistrationName = "SEND_EMAIL_SBX"

# the mailbox you only want to send from
$TargetMailbox = "noreply@waideveloper.onmicrosoft.com"

# the name of the management scope
$ScopeName = "ScopedMailbox"

# the name of the role assignment
$RoleAssignmentName = "MailboxName_Send_RBAC"

# the permission to assign
$Role = "Application Mail.Send"


Import-Module ExchangeOnlineManagement

# Connect to Graph and Exchange Online
Connect-MgGraph -Scopes 'Application.Read.All'
Connect-ExchangeOnline

# Identify the service principal for the app
$entraSP = Get-MgServicePrincipal -Filter "DisplayName eq '$AppRegistrationName'"

# Register the service principal in Exchange Online
New-ServicePrincipal -AppId $entraSP.AppId -ObjectId $entraSP.Id -DisplayName $entraSP.DisplayName

# Target mailbox to allow read access to
$mailbox = Get-Mailbox -Identity $TargetMailbox

# Create a custom management scope limited to that mailbox
New-ManagementScope -Name $ScopeName -RecipientRestrictionFilter "Guid -eq '$($mailbox.Guid)'"

# Assign 'Application Mail.Send' role to the app, scoped to just that mailbox
New-ManagementRoleAssignment -App $entraSP.AppId `
    -Role $Role `
    -CustomResourceScope $ScopeName `
    -Name $RoleAssignmentName

# Verify scoped permission
Test-ServicePrincipalAuthorization $entraSP.AppId -Resource $mailbox