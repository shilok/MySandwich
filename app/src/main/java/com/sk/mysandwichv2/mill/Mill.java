package com.sk.mysandwichv2.mill;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mill implements Serializable, Parcelable {
    private String name;
    private String description;
    private int price;
    private String img;
    private boolean selected;

    public Mill(String name, String description, int price, String img, boolean selected, List<Ingredients> ingredients, List<Drinks> drinks, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.img = img;
        this.selected = selected;
        this.ingredients = ingredients;
        this.drinks = drinks;
        this.category = category;
    }

    protected Mill(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        img = in.readString();
        category = in.readString();
    }

    public static final Creator<Mill> CREATOR = new Creator<Mill>() {
        @Override
        public Mill createFromParcel(Parcel in) {
            return new Mill(in);
        }

        @Override
        public Mill[] newArray(int size) {
            return new Mill[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(img);
        dest.writeString(category);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setDrinks(List<Drinks> drinks) {
        this.drinks = drinks;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIngredients(List<Ingredients> ingredients) {

        this.ingredients = ingredients;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private List<Ingredients> ingredients;
    private List<Drinks> drinks;
    private String category;

    public Mill() {
    }

    public List<Drinks> getDrinks() {
        return drinks;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("description", description);
        result.put("price", price);
        result.put("ingredients", ingredients);
        result.put("drinks", drinks);
        result.put("category", category);
        return result;
    }

    @Override
    public String toString() {
        return "Mill{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", img='" + img + '\'' +
                ", ingredients=" + ingredients +
                ", drinks=" + drinks +
                ", category='" + category + '\'' +
                '}';
    }


}

