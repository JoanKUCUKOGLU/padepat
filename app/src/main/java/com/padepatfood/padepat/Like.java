package com.padepatfood.padepat;

public class Like {

    private Integer likeid;
    private String deviceid;
    private Integer recipeid;
    private String type;

    public Like() {
    }

    public Like(Integer likeid, String deviceid, Integer recipeid, String type) {
        this.likeid = likeid;
        this.deviceid = deviceid;
        this.recipeid = recipeid;
        this.type = type;
    }

    public Integer getLikeid() {
        return likeid;
    }

    public String getDeviceid() {
        return deviceid;
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
