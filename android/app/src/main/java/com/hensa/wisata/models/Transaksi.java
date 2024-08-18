package com.hensa.wisata.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Transaksi {
    @Expose
    @SerializedName("id")
    protected Number id;

    @Expose
    @SerializedName("kode")
    protected String kode;

    @Expose
    @SerializedName("pelanggan")
    protected Pelanggan pelanggan;

    @Expose
    @SerializedName("waktu")
    protected String waktu;

    @Expose
    @SerializedName("status")
    protected String status;

    @Expose
    @SerializedName("harga")
    protected Number harga;

    @Expose
    @SerializedName("penerbangan")
    protected Penerbangan penerbangan;

    @Expose
    @SerializedName("penumpang")
    protected List<Penumpang> penumpang;

    @Expose
    @SerializedName("pembayaran")
    protected List<Pembayaran> pembayaran;

    public Number getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public String getWaktu() {
        return LocalDateTime.parse(waktu, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
    }

    public String getStatus() {
        return status;
    }

    public Number getHarga() {
        return harga;
    }

    public void setHarga(Number harga) {
        this.harga = harga;
    }

    public Penerbangan getPenerbangan() {
        return penerbangan;
    }

    public void setPenerbangan(Penerbangan penerbangan) {
        this.penerbangan = penerbangan;
    }

    public List<Penumpang> getPenumpang() {
        return penumpang;
    }

    public List<Pembayaran> getPembayaran() {
        return pembayaran;
    }

    public static class Penerbangan {
        @Expose
        @SerializedName("id")
        protected Number id;

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

        public Number getId() {
            return id;
        }

        public String getKode() {
            return kode;
        }

        public void setKode(String kode) {
            this.kode = kode;
        }

        public String getBerangkat() {
            return berangkat;
        }

        public String getTujuan() {
            return tujuan;
        }

        public String getWaktuBerangkat() {
            return waktu_berangkat;
        }

        public void setWaktuBerangkat(String waktu_berangkat) {
            this.waktu_berangkat = waktu_berangkat;
        }

        public String getWaktuTiba() {
            return waktu_tiba;
        }

        public void setWaktuTiba(String waktu_tiba) {
            this.waktu_tiba = waktu_tiba;
        }
    }

    public static class Pembayaran {
        @Expose
        @SerializedName("id")
        protected Number id;

        @Expose
        @SerializedName("jumlah")
        protected Number jumlah;

        @Expose
        @SerializedName("waktu")
        protected String waktu;

        @Expose
        @SerializedName("bukti")
        protected String bukti;

        @Expose
        @SerializedName("status")
        protected String status;

        public Number getId() {
            return id;
        }

        public Number getJumlah() {
            return jumlah;
        }

        public String getWaktu() {
            return waktu;
        }

        public String getBukti() {
            return bukti;
        }

        public String getStatus() {
            return status;
        }
    }
}
