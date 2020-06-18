package com.padepatfood.padepat;

public class Comment {

    private Integer id;
    private String content;
    private Integer recipeId;
    private Integer userId;

    public Comment() {
    }

    public Comment(Integer id, String content, Integer recipeId, Integer userId) {
        this.id = id;
        this.content = content;
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
