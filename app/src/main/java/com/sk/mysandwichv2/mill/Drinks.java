package com.sk.mysandwichv2.mill;

public class Drinks {
    String name;
    String img;
    int price;
    boolean isSelected;

    public Drinks() {
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "Drinks{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", price=" + price +
                ", isSelected=" + isSelected +
                '}';
    }
}
