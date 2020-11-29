package com.lazysecs.meetingapp.models;

import java.util.List;

public class MegaCategory {
    private String name;
    private List<Category> categories;
    private boolean expanded;

    public MegaCategory(String name, List<Category> categories) {
        this.name = name;
        this.categories = categories;
        this.expanded = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
