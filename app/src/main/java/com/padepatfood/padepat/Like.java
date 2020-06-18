package com.padepatfood.padepat;

public class Like {

    private Integer likeid;
    private Integer userid;
    private Integer recipeid;
    private String type;

    public Like() {
    }

    public Like(Integer likeid, Integer userid, Integer recipeid, String type) {
        this.likeid = likeid;
        this.userid = userid;
        this.recipeid = recipeid;
        this.type = type;
    }

    public Integer getLikeid() {
        return likeid;
    }

    public Integer getUserid() {
        return userid;
    }

    public Integer getRecipeid() {
        return recipeid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
