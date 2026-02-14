package com.blackspider.todo;

public class Task {
    private int id;
    private String title;
    private String dueDate;
    private String category;

    public Task(int id, String title, String dueDate, String category) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.category = category;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
