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

        context.getLogger().log(Level.INFO, "SQL Changes: " + new Gson().toJson(employeeItems));
    }
}