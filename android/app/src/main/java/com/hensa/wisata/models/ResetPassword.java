package com.hensa.wisata.models;

import com.google.gson.annotations.SerializedName;

public class ResetPassword {
    @SerializedName("password")
    private String password;
    @SerializedName("new_password")
    private String new_password;
    @SerializedName("confirm_password")
    private String confirm_password;

    public ResetPassword(String password, String new_password, String confirm_password){
        this.password = password;
        this.new_password = new_password;
        this.confirm_password = confirm_password;
    }
}
