package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.google.gson.Gson;

import java.util.Optional;
import java.util.logging.Level;

public class ProductsTrigger {
    @FunctionName("ToDoProcessor")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {

        context.getLogger().log(Level.INFO, "Java HTTP trigger processed a request.");

        // Parse the request body to get SQL change data
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass SQL change data on the query string or in the request body")
                    .build();
        } else {
            // Create sample SQL change data
            ToDoItem todoItem = new ToDoItem(1, "Sample Task", "This is a sample task", false);
            SqlChangeToDoItem change = new SqlChangeToDoItem(todoItem, SqlChangeOperation.Insert);
            
            context.getLogger().log(Level.INFO, "SQL Change processed: " + new Gson().toJson(change));
            
            return request.createResponseBuilder(HttpStatus.OK)
                    .body("SQL change processed: " + new Gson().toJson(change))
                    .build();
        }
    }
}