package com.example.wyf.suidaodemo.database.entity;

import java.io.Serializable;

public class PicItemEntity implements Serializable {
    private static final long serialVersionUID = 7978326586380224946L;

    private String msg;
    private String imagePath;

    public PicItemEntity(String msg, String imagePath) {
        this.msg = msg;
        this.imagePath = imagePath;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "{msg='" + msg + '\'' +
                ",imagePath='" + imagePath + '\'' +
                "}";
    }
}
