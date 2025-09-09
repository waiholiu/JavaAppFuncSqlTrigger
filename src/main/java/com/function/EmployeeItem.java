package com.function;

import java.time.LocalDate;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class EmployeeItem {
    public int id;
    public String name;
    
    @JsonAdapter(LocalDateDeserializer.class)
    public LocalDate hireDate;

    public EmployeeItem() {
    }

    public EmployeeItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    // Custom deserializer for LocalDate to handle Azure Functions SQL trigger JSON
    public static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
                throws JsonParseException {
            try {
                return json != null && !json.isJsonNull() ? LocalDate.parse(json.getAsString()) : null;
            } catch (Exception e) {
                // If parsing fails, return null instead of throwing exception
                return null;
            }
        }
    }
}
