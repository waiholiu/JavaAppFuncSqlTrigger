package com.function;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.EmailAddress;
import java.util.LinkedList;
import java.util.logging.Logger;

public class GraphEmailService {
    private final GraphServiceClient graphServiceClient;
    private final String fromUserId;
    private final Logger logger;

    // Default constructor that reads from environment variables
    public GraphEmailService() {
        this(
            System.getenv("AZURE_T_ID"),
            System.getenv("AZURE_CLIENT_ID"), 
            System.getenv("AZURE_CLIENT_SECRET"),
            System.getenv("GRAPH_FROM_USER_ID"),
            Logger.getLogger(GraphEmailService.class.getName())
        );
    }

    public GraphEmailService(String tenantId, String clientId, String clientSecret, String fromUserId, Logger logger) {
        this.fromUserId = fromUserId;
        this.logger = logger;

        // Validate required parameters - fromUserId is always required
        if (fromUserId == null) {
            throw new IllegalArgumentException("Missing required Graph API configuration. Please set GRAPH_FROM_USER_ID environment variable.");
        }

        TokenCredential credential;
        
        // Check if all app registration credentials are provided
        if (tenantId != null && clientId != null && clientSecret != null) {
            // Use ClientSecretCredential when explicit credentials are provided
            logger.info("Using ClientSecretCredential with provided app registration details");
            credential = new ClientSecretCredentialBuilder()
                    .tenantId(tenantId)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        } else {
            // Use DefaultAzureCredential for managed identity or other default auth methods
            logger.info("Using DefaultAzureCredential (managed identity or default Azure authentication)");
            credential = new DefaultAzureCredentialBuilder().build();
        }

        // Initialize Graph Service Client
        this.graphServiceClient = new GraphServiceClient(credential);
        
        logger.info("GraphServiceClient initialized successfully");
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            logger.info("Preparing to send email to: " + toEmail);

            // Create the SendMailPostRequestBody
            com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody sendMailBody = 
                new com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody();

            // Create the message
            Message message = new Message();
            message.setSubject(subject);
            
            // Set message body
            ItemBody messageBody = new ItemBody();
            messageBody.setContentType(BodyType.Html);
            messageBody.setContent(body);
            message.setBody(messageBody);
            
            // Set recipients
            LinkedList<Recipient> toRecipients = new LinkedList<Recipient>();
            Recipient recipient = new Recipient();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.setAddress(toEmail);
            recipient.setEmailAddress(emailAddress);
            toRecipients.add(recipient);
            message.setToRecipients(toRecipients);
            
            // Set the message and save to sent items
            sendMailBody.setMessage(message);
            sendMailBody.setSaveToSentItems(true);
            
            logger.info("Sending email via Microsoft Graph API...");
            
            // Send the email using the Graph SDK
            graphServiceClient.users().byUserId(fromUserId).sendMail().post(sendMailBody);
            
            logger.info("Email sent successfully to: " + toEmail);

        } catch (Exception e) {
            logger.severe("Error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
