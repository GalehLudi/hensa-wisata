package com.hensa.wisata.libs;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences preferences;
    private static final String KEY_TOKEN = "token";

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences("HensaWisata", Context.MODE_PRIVATE);
    }

    public void setToken(String token) {
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }
}
