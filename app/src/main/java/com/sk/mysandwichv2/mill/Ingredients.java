package com.sk.mysandwichv2.mill;

public class Ingredients{
    private String name;
    private int price;
    private String img;
    public boolean isSelected;


    public Ingredients() {
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Ingredients{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", img='" + img + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
