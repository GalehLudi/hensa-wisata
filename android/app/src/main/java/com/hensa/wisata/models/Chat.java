package com.hensa.wisata.models;

import com.github.javafaker.Faker;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Chat {
    @Expose
    @SerializedName("id")
    protected int id;

    @Expose
    @SerializedName("user")
    protected User user;

    @Expose
    @SerializedName("pelanggan")
    protected Pelanggan pelanggan;

    @Expose
    @SerializedName("pesan")
    protected String pesan;

    @Expose
    @SerializedName("file")
    protected String file;

    @Expose
    @SerializedName("status")
    protected String status;

    @Expose
    @SerializedName("dari")
    protected String dari;

    @Expose
    @SerializedName("created_at")
    protected String created_at;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public String getDari() {
        return dari;
    }

    public void setDateTime() {
        this.created_at = LocalDateTime.now().toString();
    }

    public String getDate() {
        return this.parseDateTime("dd MMM yyyy");
    }

    public String getTime() {
        return this.parseDateTime("HH:mm");
    }

    private String parseDateTime(String format) {
        return OffsetDateTime.parse(created_at).format(DateTimeFormatter.ofPattern(format));
    }

    public void fake() {
        Faker faker = new Faker(new Locale("id", "ID"));
        User user = new User();
        user.fake();
        this.user = user;
        this.id = faker.number().numberBetween(1, 100);
        this.pesan = faker.lorem().sentence();
        this.status = faker.options().option("read", "unread");
        this.dari = faker.options().option("pelanggan", "user");
//        this.file = faker.file().fileName();
        this.created_at = faker.date().future(30, TimeUnit.DAYS).toString();
        this.setDateTime();
    }
}
