package com.hensa.wisata.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {
    @Expose
    @SerializedName("user")
    protected String user;
    @Expose
    @SerializedName("password")
    protected String password;
    @Expose
    @SerializedName("device")
    protected String device;

    @SuppressLint("HardwareIds")
    public Auth(Context context, String user, String password){
        this.user = user;
        this.password = password;
        this.device = Build.BRAND+ " "+ Build.MODEL +" | "+Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
