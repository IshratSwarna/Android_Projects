package com.example.passwordauthentication;

import android.graphics.drawable.Drawable;

public class AppList {
    private String name;
    Drawable icon;
    int locked;
    String package_name;

    public AppList(String str, Drawable drawable, int i, String str2) {
        this.name = str;
        this.icon = drawable;
        this.locked = i;
        this.package_name = str2;
    }

    public String getName() { return this.name;
    }

    public void setName(String str) { this.name = str;
    }

    public Drawable getIcon() { return this.icon;
    }

    public void setIcon(Drawable drawable) { this.icon = drawable;
    }

    public int getLocked() { return this.locked;
    }

    public void setLocked(int i) { this.locked = i;
    }

    public String getPackage_name() { return this.package_name;
    }

    public void setPackage_name(String str) { this.package_name = str;
    }
}
