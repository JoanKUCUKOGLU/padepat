package com.padepatfood.padepat;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private List<Integer> favoriteRecipes;
    private String imgLink;
    private String dietaryPrefs;

    public User() {
    }

    public User(Integer id, String name, String email, String password,String imgLink, String dietaryPrefs) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        favoriteRecipes = new ArrayList<>();
        this.imgLink = imgLink;
        this.dietaryPrefs = dietaryPrefs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<Integer> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getDietaryPrefs() {
        return dietaryPrefs;
    }

    public void setDietaryPrefs(String dietaryPrefs) {
        this.dietaryPrefs = dietaryPrefs;
    }
}
