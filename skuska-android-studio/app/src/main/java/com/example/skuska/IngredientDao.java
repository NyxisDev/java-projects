package com.example.skuska;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Ingredient> ingredients);

    @Insert
    void insert(Ingredient ingredient);

    @Query("SELECT * FROM ingredients WHERE meal_id = :mealId")
    List<Ingredient> getIngredientsForMeal(int mealId);

    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredients();

    @Query("SELECT meal_id FROM ingredients WHERE nazov_ing = :name")
    List<Integer> getMealIdByName(String name);

    @Query("SELECT SUM(cena) FROM ingredients WHERE meal_id = :mealId")
    double getTotalPriceForMeal(int mealId);

    @Query("DELETE FROM ingredients WHERE meal_id = :mealId")
    void deleteByMealId(int mealId);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);
}
