package com.seoul.ddroad.diary;

public class SingerItem {


    String title;
    String content;
    int listId;
    int resId;

    public SingerItem(){}

    public SingerItem(int resId) {
        this.resId = resId;
    }

    public SingerItem(String title,int listId, int resId) {
        this.listId = listId;
        this.title = title;
        this.resId = resId;
    }

    public SingerItem(String title,String content,int listId, int resId) {
        this.listId = listId;
        this.title = title;
        this.content = content;
        this.resId = resId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }
}
