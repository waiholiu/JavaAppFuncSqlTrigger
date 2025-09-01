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
                    context.getLogger().log(Level.INFO, "Operation: " + (change.operation != null ? change.operation : "null"));
                    context.getLogger().log(Level.INFO, "Item is null: " + (change.item == null));
                    
                    if (change.item != null) {
                        context.getLogger().log(Level.INFO, "ID: " + change.item.id);
                        context.getLogger().log(Level.INFO, "Name: " + change.item.name);
                    } else {
                        context.getLogger().log(Level.INFO, "Item object is NULL - deserialization issue!");
                    }
                } else {
                    context.getLogger().log(Level.INFO, "Change object is NULL!");
                }
            }
        } else {
            context.getLogger().log(Level.INFO, "employeeItems array is NULL!");
        }
        
        context.getLogger().log(Level.INFO, "SQL Changes: " + new Gson().toJson(employeeItems));
        context.getLogger().log(Level.INFO, "=== END SQL TRIGGER ===");
    }
}