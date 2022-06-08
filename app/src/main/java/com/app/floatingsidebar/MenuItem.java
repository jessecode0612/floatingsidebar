package com.app.floatingsidebar;

import android.graphics.drawable.Drawable;

public class MenuItem {
    int id;
    String title;
    String packageName;
    Drawable icon;

    public MenuItem(int id, String title, String packageName, Drawable icon) {
        this.id = id;
        this.title = title;
        this.packageName = packageName;
        this.icon = icon;
    }
}
