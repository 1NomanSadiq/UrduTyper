package me.nomi.urdutyper;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String PREFERENCES = "login_data";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;


    public Prefs(Context context) {
        this.context = context;
        settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public String getStringEntry(String key) {
        return settings.getString(key, "");
    }

    public void setStringEntry(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

}