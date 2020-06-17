package com.padepatfood.padepat;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    private static GlobalData instance;

    private Context context;

    private Boolean connected;

    private List<Recipe> recipeList;
    private List<String> categoryList;

    private User currentUser;

    private GlobalData() {
        recipeList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Boolean isConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
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

    public Boolean isUserLogged() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static synchronized GlobalData getInstance() {
        if(instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
}
