package com.android.smartvirtualid.datasource;

import android.content.SharedPreferences;

import com.android.smartvirtualid.utils.Constants;

import javax.inject.Inject;

public class SharedPreferencesDataSource {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    @Inject
    public SharedPreferencesDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void saveId(String userId) {
        editor.putString(Constants.USER_ID, userId);
        editor.apply();
    }

    public String getId() {
        return sharedPreferences.getString(Constants.USER_ID, null);
    }

    public void setRole(String role) {
        editor.putString(Constants.ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return sharedPreferences.getString(Constants.ROLE, null);
    }


    public void removeAllValues() {
        for (String key : sharedPreferences.getAll().keySet()) {
            editor.remove(key);
            editor.apply();
        }
    }
}
