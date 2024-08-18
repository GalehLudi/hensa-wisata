package com.hensa.wisata.models;

public class Penumpang {
    private final String nama;
    private final int umur;

    public Penumpang(String nama, int umur) {
        this.nama = nama;
        this.umur = umur;
    }

    public String getNama() {
        return nama;
    }

    public int getUmur() {
        return umur;
    }
}
