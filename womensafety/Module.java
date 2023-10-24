package com.example.womensafety;

import android.app.Application;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class Module extends Application {

    public ArrayList<String> arr=new ArrayList<>();
    public contactInfoAdaptor contactInfoAdaptor;

    public String gvalueId;
    public String gvalueName;


    public String getGvalueId() {
        return gvalueId;
    }

    public void setGvalueId(String gvalueId) {
        this.gvalueId = gvalueId;
    }

    public String getGvalueName() {
        return gvalueName;
    }

    public void setGvalueName(String gvalueName) {
        this.gvalueName = gvalueName;
    }
}
