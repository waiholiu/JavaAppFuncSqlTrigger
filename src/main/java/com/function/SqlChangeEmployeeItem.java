package com.function;

public class SqlChangeEmployeeItem {
    public EmployeeItem item;
    public SqlChangeOperation operation;

    public SqlChangeEmployeeItem() {
    }

    public SqlChangeEmployeeItem(EmployeeItem item, SqlChangeOperation operation) {
        this.item = item;
        this.operation = operation;
    }
}
