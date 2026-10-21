package com.example.skuska;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients",
        foreignKeys = @ForeignKey(
                entity = Meal.class,
                parentColumns = "id",
                childColumns = "meal_id",
                onDelete = ForeignKey.CASCADE
        )
)

public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "meal_id")
    private int meal_id;
    private String nazov_ing;
    private double cena;
    private String hmotnost;

    public Ingredient(int meal_id, String nazov_ing, double cena, String hmotnost) {
        this.meal_id = meal_id;
        this.nazov_ing = nazov_ing;
        this.cena = cena;
        this.hmotnost = hmotnost;
    }

    public int getId() {
        return id;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public String getNazov_ing() {
        return nazov_ing;
    }

    public double getCena() {
        return cena;
    }

    public String getHmotnost() {
        return hmotnost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public void setNazov_ing(String nazov_ing) {
        this.nazov_ing = nazov_ing;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public void setHmotnost(String hmotnost) {
        this.hmotnost = hmotnost;
    }
}
