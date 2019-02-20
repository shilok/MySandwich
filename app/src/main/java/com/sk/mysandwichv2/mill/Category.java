package com.sk.mysandwichv2.mill;

import java.io.Serializable;

public class Category implements Serializable {
    String name;

    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "name='" + name + '\'' +
                '}';
    }
}
