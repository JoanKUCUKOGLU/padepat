package com.padepatfood.padepat;

public class Like {

    private String deviceid;
    private Integer recipeid;
    private String type;

    public Like() {
    }

    public Like(String deviceid, Integer recipeid, String type) {
        this.deviceid = deviceid;
        this.recipeid = recipeid;
        this.type = type;
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
}
