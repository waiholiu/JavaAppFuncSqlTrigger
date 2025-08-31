package com.function;

public class SqlChangeToDoItem {
    public ToDoItem item;
    public SqlChangeOperation operation;

    public SqlChangeToDoItem() {
    }

    public SqlChangeToDoItem(ToDoItem item, SqlChangeOperation operation) {
        this.item = item;
        this.operation = operation;
    }
}