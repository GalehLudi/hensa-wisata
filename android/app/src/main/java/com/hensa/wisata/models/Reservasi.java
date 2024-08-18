package com.hensa.wisata.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reservasi {
    @Expose
    @SerializedName("kode")
    protected String kode;

    @Expose
    @SerializedName("berangkat")
    protected String berangkat;

    @Expose
    @SerializedName("tujuan")
    protected String tujuan;

    @Expose
    @SerializedName("waktu_berangkat")
    protected String waktu_berangkat;

    @Expose
    @SerializedName("waktu_tiba")
    protected String waktu_tiba;

    @Expose
    @SerializedName("pelanggan")
    protected String pelanggan;

    @Expose
    @SerializedName("harga")
    protected String harga;

    @Expose
    @SerializedName("penumpang")
    protected List<Penumpang> penumpang;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getBerangkat() {
        return berangkat;
    }

    public void setBerangkat(String berangkat) {
        this.berangkat = berangkat;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getWaktu_berangkat() {
        return waktu_berangkat;
    }

    public void setWaktuBerangkat(String waktu_berangkat) {
        this.waktu_berangkat = waktu_berangkat;
    }

    public String getWaktu_tiba() {
        return waktu_tiba;
    }

    public void setWaktuTiba(String waktu_tiba) {
        this.waktu_tiba = waktu_tiba;
    }

    public String getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(String pelanggan) {
        this.pelanggan = pelanggan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public List<Penumpang> getPenumpang() {
        return penumpang;
    }

    public void setPenumpang(List<Penumpang> penumpang) {
        this.penumpang = penumpang;
    }
}
