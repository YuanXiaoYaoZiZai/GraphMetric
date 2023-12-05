package com.fsc.metric.model;

public class GraphCategory {

    private String name;

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof GraphCategory) {
            return name.equals(((GraphCategory) obj).getName());
        } else {
            return false;
        }
    }

    public GraphCategory() {
    }

    public GraphCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
