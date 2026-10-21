package com.example.skuska;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey
    private int id;
    private String nazov;
    private String typ;

    public Meal(int id, String nazov, String typ) {
        this.id = id;
        this.nazov = nazov;
        this.typ = typ;
    }

    public int getId() {
        return id;
    }

    public String getNazov() {
        return nazov;
    }

    public String getTyp() {
        return typ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
}
