package com.seoul.ddroad;

import android.app.Application;

public class AppCustom extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //FontsOverride.setDefaultFont(this, "DEFAULT", "font/nanumbrush.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font/nanumpen.ttf");
        //FontsOverride.setDefaultFont(this, "SERIF", "font/nanumbrush.ttf");
        //FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/nanumbrush.ttf");
    }
}