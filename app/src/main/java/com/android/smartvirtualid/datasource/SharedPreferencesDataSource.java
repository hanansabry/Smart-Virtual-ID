package com.android.smartvirtualid.datasource;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPreferencesDataSource {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    @Inject
    public SharedPreferencesDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void removeAllValues() {
        for (String key : sharedPreferences.getAll().keySet()) {
            editor.remove(key);
            editor.apply();
        }
    }
}
