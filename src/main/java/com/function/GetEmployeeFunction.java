package com.function;

import java.util.Optional;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.google.gson.Gson;

/**
 * Azure Functions with HTTP Trigger to get employee information.
 */
public class GetEmployeeFunction {
    private static final Gson gson = new Gson();
    private static final String PLACEHOLDER_MESSAGE = "Employee lookup functionality. Connect to database to retrieve employee details.";
    private static final String PLACEHOLDER_NOTE = "This is a placeholder response. Implement database query logic as needed.";
    
    /**
     * This function listens at endpoint "/api/GetEmployee". 
     * It accepts a GET request with an employee ID as a query parameter.
     * Example: curl {your host}/api/GetEmployee?id=1
     */
    @FunctionName("GetEmployee")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req", 
                methods = {HttpMethod.GET}, 
                authLevel = AuthorizationLevel.FUNCTION
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("GetEmployee function processed a request.");

        // Parse query parameter
        String employeeId = request.getQueryParameters().get("id");

        if (employeeId == null || employeeId.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Please pass an employee id on the query string: ?id=<employeeId>")
                .build();
        }

        try {
            // Validate that the id is a number
            int id = Integer.parseInt(employeeId);
            
            // In a real application, you would query the database here
            // For this example, we'll return a mock response
            EmployeeResponse response = new EmployeeResponse(
                id, 
                PLACEHOLDER_MESSAGE,
                PLACEHOLDER_NOTE
            );
            
            context.getLogger().info("Successfully processed request for employee ID: " + id);
            
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(gson.toJson(response))
                .build();
                
        } catch (NumberFormatException e) {
            context.getLogger().warning("Invalid employee ID format: " + employeeId);
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid employee ID format. Please provide a numeric ID.")
                .build();
        }
    }
    
    /**
     * Response class for employee lookup
     */
    private static class EmployeeResponse {
        private final int id;
        private final String message;
        private final String note;
        
        public EmployeeResponse(int id, String message, String note) {
            this.id = id;
            this.message = message;
            this.note = note;
        }
        
        public int getId() {
            return id;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getNote() {
            return note;
        }
    }
}
