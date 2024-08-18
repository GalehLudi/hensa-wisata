package com.hensa.wisata.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pelanggan {
    @Expose
    @SerializedName("id")
    protected Number id;

    @Expose
    @SerializedName("nama")
    protected String nama;

    @Expose
    @SerializedName("hp")
    protected String hp;

    public Number getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }
}
