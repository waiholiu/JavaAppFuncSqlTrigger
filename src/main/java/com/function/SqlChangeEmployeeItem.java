package com.function;

public class SqlChangeEmployeeItem {
    public EmployeeItem Item;    // Uppercase I to match JSON
    public SqlChangeOperation Operation;  // Uppercase O to match JSON

    public SqlChangeEmployeeItem() {
        // Default constructor required for deserialization
    }

    public SqlChangeEmployeeItem(EmployeeItem Item, SqlChangeOperation Operation) {
        this.Item = Item;
        this.Operation = Operation;
    }

    public EmployeeItem getItem() {
        return Item;
    }

    public void setItem(EmployeeItem Item) {
        this.Item = Item;
    }

    public SqlChangeOperation getOperation() {
        return Operation;
    }

    public void setOperation(SqlChangeOperation Operation) {
        this.Operation = Operation;
    }
}
