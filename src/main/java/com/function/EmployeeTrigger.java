package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.sql.annotation.SQLTrigger;
import com.google.gson.Gson;

import java.util.logging.Level;

public class EmployeeTrigger {

    @FunctionName("EmployeeTrigger")
    public void run(
            @SQLTrigger(name = "employeeItems", tableName = "[dbo].[employee]", connectionStringSetting = "SqlConnectionString") SqlChangeEmployeeItem[] employeeItems,
            ExecutionContext context) {

        context.getLogger().log(Level.INFO, "=== SQL TRIGGER FIRED ===");
        context.getLogger().log(Level.INFO,
                "Received " + (employeeItems != null ? employeeItems.length : "null") + " employee items");

        for (int i = 0; i < employeeItems.length; i++) {
            SqlChangeEmployeeItem change = employeeItems[i];

            // Send email only for INSERT operations
            if (change.Operation == SqlChangeOperation.Insert) {
                context.getLogger().log(Level.INFO,
                        "New employee inserted: " + change.Item.name + " (ID: " + change.Item.id + ")");

                try {
                    GraphEmailService emailService = new GraphEmailService();
                    String subject = "New Employee Added: " + change.Item.name;
                    String body = String.format(
                            "A new employee has been added to the system:\n\n" +
                                    "Name: %s\n" +
                                    "ID: %d\n\n" +
                                    "This is an automated notification from the Employee Management System.",
                            change.Item.name, change.Item.id);

                    // Send to a configured recipient (you can make this configurable via app
                    // settings)
                    String recipientEmail = System.getenv("NOTIFICATION_EMAIL");
                    if (recipientEmail != null && !recipientEmail.isEmpty()) {
                        emailService.sendEmail(recipientEmail, subject, body);
                        context.getLogger().log(Level.INFO,
                                "Email notification sent successfully to: " + recipientEmail);
                    } else {
                        context.getLogger().log(Level.WARNING,
                                "NOTIFICATION_EMAIL not configured - skipping email notification");
                    }
                } catch (Exception e) {
                    context.getLogger().log(Level.SEVERE,
                            "Failed to send email notification: " + e.getMessage());
                }

            }
        }

        context.getLogger().log(Level.INFO, "SQL Changes: " + new Gson().toJson(employeeItems));
        context.getLogger().log(Level.INFO, "=== END SQL TRIGGER ===");
    }
}