package com.blackspider.todo;

public class Category {
    private int id;
    private String name;
    private int color;

    public Category(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
