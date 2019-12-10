package co.edu.aiteaching.drawords.models;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ElementCategory {

    private String name;
    private String path;
    private String category;

    public ElementCategory(String name, String path, String category){
        this.name = name;
        this.path = path;
        this.category = category;
    }

    public ElementCategory(){}

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getCategory(){
        return category;
    }
}
