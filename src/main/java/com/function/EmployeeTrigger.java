package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.sql.annotation.SQLTrigger;
import com.google.gson.Gson;

import java.util.logging.Level;

public class EmployeeTrigger {
    
    @FunctionName("EmployeeTrigger")
    public void run(
            @SQLTrigger(
                name = "employeeItems",
                tableName = "[dbo].[employee]",
                connectionStringSetting = "SqlConnectionString")
                SqlChangeEmployeeItem[] employeeItems,
            ExecutionContext context) {

        context.getLogger().log(Level.INFO, "=== SQL TRIGGER FIRED ===");
        context.getLogger().log(Level.INFO, "Received " + (employeeItems != null ? employeeItems.length : "null") + " employee items");
        
        if (employeeItems != null) {
            for (int i = 0; i < employeeItems.length; i++) {
                SqlChangeEmployeeItem change = employeeItems[i];
                context.getLogger().log(Level.INFO, "=== Processing Item " + i + " ===");
                
                if (change != null) {
                    context.getLogger().log(Level.INFO, "Operation: " + (change.Operation != null ? change.Operation : "null"));
                    context.getLogger().log(Level.INFO, "Item is null: " + (change.Item == null));
                    
                    if (change.Item != null) {
                        context.getLogger().log(Level.INFO, "Employee ID: " + change.Item.id);
                        context.getLogger().log(Level.INFO, "Employee Name: " + change.Item.name);
                        
                        // Send email only for INSERT operations
                        if (change.Operation == SqlChangeOperation.Insert) {
                            context.getLogger().log(Level.INFO, "New employee inserted: " + change.Item.name + " (ID: " + change.Item.id + ")");
                            // TODO: Add email sending logic here
                        }
                    } else {
                        context.getLogger().log(Level.WARNING, "Item object is NULL!");
                    }
                } else {
                    context.getLogger().log(Level.WARNING, "Change object is NULL!");
                }
            }
        } else {
            context.getLogger().log(Level.INFO, "employeeItems array is NULL!");
        }
        
        context.getLogger().log(Level.INFO, "SQL Changes: " + new Gson().toJson(employeeItems));
        context.getLogger().log(Level.INFO, "=== END SQL TRIGGER ===");
    }
}