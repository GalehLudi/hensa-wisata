package com.hensa.wisata.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ResponseBody<T> {
    @SerializedName("status")
    public String status;

    @Nullable
    @SerializedName("pesan")
    public String pesan;

    @Nullable
    @SerializedName("error")
    public Map<String, String> error;

    @Nullable
    @SerializedName("token")
    public String token;

    @Nullable
    @SerializedName("data")
    public T data;

    public ResponseBody(String status, String pesan) {
        this.status = status;
        this.pesan = pesan;
    }

    public String getStatus() {
        return status;
    }

    @Nullable
    public String getPesan() {
        return pesan;
    }

    @Nullable
    public Map<String, String> getError() {
        return error;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @Nullable
    public T getData() {
        return data;
    }
}
