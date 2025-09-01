package com.function;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GraphEmailService {
    private final ClientSecretCredential credential;
    private final String fromUserId;
    private final Logger logger;
    private final HttpClient httpClient;
    private final Gson gson;

    public GraphEmailService(String tenantId, String clientId, String clientSecret, String fromUserId, Logger logger) {
        this.fromUserId = fromUserId;
        this.logger = logger;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();

        // Create credential using app registration
        this.credential = new ClientSecretCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // Get access token
            TokenRequestContext tokenContext = new TokenRequestContext();
            tokenContext.addScopes("https://graph.microsoft.com/.default");
            AccessToken token = credential.getToken(tokenContext).block();
            
            if (token == null) {
                throw new RuntimeException("Failed to obtain access token");
            }

            // Create email JSON payload
            JsonObject emailJson = createEmailJson(toEmail, subject, body);
            
            // Send HTTP request to Microsoft Graph
            String graphUrl = String.format("https://graph.microsoft.com/v1.0/users/%s/sendMail", fromUserId);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(graphUrl))
                    .header("Authorization", "Bearer " + token.getToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(emailJson.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 202) {
                logger.info("Email sent successfully to: " + toEmail);
            } else {
                logger.severe("Failed to send email. Status: " + response.statusCode() + ", Response: " + response.body());
                throw new RuntimeException("Failed to send email. Status: " + response.statusCode());
            }

        } catch (Exception e) {
            logger.severe("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private JsonObject createEmailJson(String toEmail, String subject, String body) {
        JsonObject emailJson = new JsonObject();
        JsonObject message = new JsonObject();
        
        // Set subject
        message.addProperty("subject", subject);
        
        // Set body
        JsonObject bodyObj = new JsonObject();
        bodyObj.addProperty("contentType", "HTML");
        bodyObj.addProperty("content", body);
        message.add("body", bodyObj);
        
        // Set recipients
        JsonObject toRecipient = new JsonObject();
        JsonObject emailAddress = new JsonObject();
        emailAddress.addProperty("address", toEmail);
        toRecipient.add("emailAddress", emailAddress);
        
        message.add("toRecipients", gson.toJsonTree(Arrays.asList(toRecipient)));
        
        emailJson.add("message", message);
        emailJson.addProperty("saveToSentItems", true);
        
        return emailJson;
    }
}
