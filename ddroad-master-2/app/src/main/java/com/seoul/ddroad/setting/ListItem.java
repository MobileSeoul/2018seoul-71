package com.seoul.ddroad.setting;

public class ListItem {


    String title;
    int resId;

    public ListItem(){}

    public ListItem(int resId) {
        this.resId = resId;
    }

    public ListItem(String title,int resId) {
        this.title = title;
        this.resId = resId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

}
