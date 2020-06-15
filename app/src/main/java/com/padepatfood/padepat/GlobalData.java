package com.padepatfood.padepat;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    private static GlobalData instance;

    private List<Recipe> recipeList;
    private List<String> categoryList;

    private GlobalData() {
        recipeList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    public static synchronized GlobalData getInstance() {
        if(instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
}
