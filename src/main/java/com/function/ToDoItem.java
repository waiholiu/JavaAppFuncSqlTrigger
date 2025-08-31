package com.function;

public class ToDoItem {
    public int Id;
    public String Title;
    public String Description;
    public boolean IsComplete;

    public ToDoItem() {
    }

    public ToDoItem(int id, String title, String description, boolean isComplete) {
        this.Id = id;
        this.Title = title;
        this.Description = description;
        this.IsComplete = isComplete;
    }
}
