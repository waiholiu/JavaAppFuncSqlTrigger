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
        context.getLogger().log(Level.INFO, "employeeItems is null: " + (employeeItems == null));
        
        if (employeeItems != null) {
            context.getLogger().log(Level.INFO, "employeeItems length: " + employeeItems.length);
            
            for (int i = 0; i < employeeItems.length; i++) {
                SqlChangeEmployeeItem change = employeeItems[i];
                context.getLogger().log(Level.INFO, "=== Processing Item " + i + " ===");
                context.getLogger().log(Level.INFO, "Change object is null: " + (change == null));
                
                if (change != null) {
                    context.getLogger().log(Level.INFO, "Operation is null: " + (change.operation == null));
                    context.getLogger().log(Level.INFO, "Item is null: " + (change.item == null));
                    
                    if (change.operation != null) {
                        context.getLogger().log(Level.INFO, "Operation: " + change.operation);
                    }
                    
                    if (change.item != null) {
                        context.getLogger().log(Level.INFO, "Employee ID: " + change.item.id);
                        context.getLogger().log(Level.INFO, "Employee Name: " + change.item.name);
                        
                        // Only log about email sending, don't actually send it yet
                        if (change.operation == SqlChangeOperation.Insert) {
                            context.getLogger().log(Level.INFO, "Would send email for new employee: " + change.item.name);
                        }
                    }
                } else {
                    context.getLogger().log(Level.WARNING, "Change object is NULL!");
                }
            }
        } else {
            context.getLogger().log(Level.INFO, "employeeItems array is NULL!");
        }
        
        context.getLogger().log(Level.INFO, "Raw JSON: " + new Gson().toJson(employeeItems));
        context.getLogger().log(Level.INFO, "=== END SQL TRIGGER ===");
    }
}