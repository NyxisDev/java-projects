package com.example.skuska;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Meal> meals);

    @Insert
    void insert(Meal meal);

    @Query("SELECT * FROM meals ORDER BY nazov ASC")
    List<Meal> getAllMeals();

    @Query("SELECT * FROM meals ORDER BY nazov DESC")
    List<Meal> getAllMealsDesc();

    @Query("SELECT * FROM meals WHERE typ = :typ ORDER BY nazov ASC")
    List<Meal> getMealsByTypeAsc(String typ);

    @Query("SELECT * FROM meals WHERE typ = :typ ORDER BY nazov DESC")
    List<Meal> getMealsByTypeDesc(String typ);

    @Query("SELECT DISTINCT typ FROM meals ORDER BY typ ASC")
    List<String> getAllTypes();

    @Query("SELECT COALESCE(MAX(id), 0) FROM meals")
    int getMaxId();

    @Query("SELECT * FROM meals WHERE id = :mealId")
    Meal getById(int mealId);

    @Query("SELECT nazov FROM meals WHERE id = :mealId")
    List<String> getNameById(int mealId);

    @Query("DELETE FROM meals WHERE id = :mealId")
    void deleteById(int mealId);

    @Update
    void update(Meal meal);

    @Delete
    void delete(Meal meal);
}
