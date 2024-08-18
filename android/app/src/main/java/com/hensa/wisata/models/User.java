package com.hensa.wisata.models;

import com.github.javafaker.Faker;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class User {
    @Expose
    @SerializedName("id")
    protected int id;

    @Expose
    @SerializedName("nama")
    protected String nama;

    @Expose
    @SerializedName("email")
    protected String email;

    @Expose
    @SerializedName("hp")
    protected String hp;

    @Expose
    @SerializedName("level")
    protected String level;

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void fake() {
        Faker faker = new Faker(new Locale("id", "ID"));
        this.id = faker.number().numberBetween(1, 100);
        this.nama = faker.name().fullName();
        this.email = faker.internet().emailAddress();
        this.hp = faker.phoneNumber().phoneNumber();
        this.level = faker.options().option("admin", "sales", "manager");
    }
}
