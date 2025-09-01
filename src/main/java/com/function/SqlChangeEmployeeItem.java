package com.function;

public class SqlChangeEmployeeItem {
    public EmployeeItem item;
    public SqlChangeOperation operation;

    public SqlChangeEmployeeItem() {
        // Default constructor required for deserialization
    }

    public SqlChangeEmployeeItem(EmployeeItem item, SqlChangeOperation operation) {
        this.item = item;
        this.operation = operation;
    }

    public EmployeeItem getItem() {
        return item;
    }

    public void setItem(EmployeeItem item) {
        this.item = item;
    }

    public SqlChangeOperation getOperation() {
        return operation;
    }

    public void setOperation(SqlChangeOperation operation) {
        this.operation = operation;
    }
}
