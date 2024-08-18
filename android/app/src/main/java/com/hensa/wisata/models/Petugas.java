package com.hensa.wisata.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Petugas extends User {
    @Expose
    @SerializedName("password")
    protected String password;

    @Expose
    @SerializedName("password_confirm")
    protected String password_confirm;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirm(String password_confirm) {
        this.password_confirm = password_confirm;
    }

    public void fake() {
        this.fake();
        this.password = "password";
        this.password_confirm = "password";
    }
}
