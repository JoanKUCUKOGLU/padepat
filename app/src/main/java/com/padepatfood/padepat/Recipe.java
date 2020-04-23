package com.padepatfood.padepat;

import java.util.List;

public class Recipe {

    private String name;
    private Integer price;
    private List<String> ingredients;
    private String steps;
    private String img;

    public Recipe() {

    }

    public Recipe(String name, Integer price, List<String> ingredients, String steps, String img) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.steps = steps;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getimg() {
        return img;
    }

    public void setimg(String img) {
        this.img = img;
    }
}
