package com.example.quizi.Model;

public class CategoryModel {
    private String id;
    private final String category_name;

    public CategoryModel(String id, String category_name) {
        this.id = id;
        this.category_name = category_name;
    }

    public String getId() {
        return id;
    }

    public String getCategory_name() {
        return category_name;
    }
}
