package com.example.skuska;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateMealActivity extends AppCompatActivity {

    private final List<Ingredient> ingredientsList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private EditText editNazov, editTyp, editIngNazov, editIngCena, editIngHmotnost;
    private LinearLayout ingredientsContainer;
    private int editMealId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_create_meal);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editNazov = findViewById(R.id.editNazov);
        editTyp = findViewById(R.id.editTyp);
        editIngNazov = findViewById(R.id.editIngNazov);
        editIngCena = findViewById(R.id.editIngCena);
        editIngHmotnost = findViewById(R.id.editIngHmotnost);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);

        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> addIngredient());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveMeal());

        editMealId = getIntent().getIntExtra("meal_id", -1);
        if (editMealId != -1) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Upraviť jedlo");
            loadExistingData();
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Nové jedlo");
        }
    }

    private void loadExistingData() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Meal meal = db.mealDao().getById(editMealId);
            List<Ingredient> existing = db.ingredientDao().getIngredientsForMeal(editMealId);

            runOnUiThread(() -> {
                editNazov.setText(meal.getNazov());
                editTyp.setText(meal.getTyp());
                for (Ingredient ing : existing) {
                    ingredientsList.add(ing);
                    addIngredientRow(ing);
                }
            });
        });
    }

    private void addIngredientRow(Ingredient ing) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView text = new TextView(this);
        text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        text.setText(String.format(java.util.Locale.getDefault(),
                "• %s — %s — %.2f €", ing.getNazov_ing(), ing.getHmotnost(), ing.getCena()));
        text.setPadding(0, 8, 0, 8);

        Button btnRemove = new Button(this);
        btnRemove.setText("✕");
        btnRemove.setOnClickListener(v -> {
            ingredientsList.remove(ing);
            ingredientsContainer.removeView(row);
        });

        row.addView(text);
        row.addView(btnRemove);
        ingredientsContainer.addView(row);
    }

    private void addIngredient() {
        String name = editIngNazov.getText().toString().trim();
        String priceStr = editIngCena.getText().toString().trim();
        String weight = editIngHmotnost.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Vyplňte všetky polia ingrediencie", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Neplatná cena", Toast.LENGTH_SHORT).show();
            return;
        }

        Ingredient ing = new Ingredient(0, name, price, weight);
        ingredientsList.add(ing);
        addIngredientRow(ing);

        editIngNazov.setText("");
        editIngCena.setText("");
        editIngHmotnost.setText("");
        editIngNazov.requestFocus();
    }

    private void saveMeal() {
        String nazov = editNazov.getText().toString().trim();
        String typ = editTyp.getText().toString().trim();

        if (nazov.isEmpty() || typ.isEmpty()) {
            Toast.makeText(this, "Vyplňte názov a typ jedla", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            int mealId;
            if (editMealId != -1) {
                mealId = editMealId;
                db.mealDao().update(new Meal(mealId, nazov, typ));
                db.ingredientDao().deleteByMealId(mealId);
            } else {
                mealId = db.mealDao().getMaxId() + 1;
                db.mealDao().insert(new Meal(mealId, nazov, typ));
            }

            for (Ingredient ing : ingredientsList) {
                ing.setMeal_id(mealId);
                ing.setId(0);
                db.ingredientDao().insert(ing);
            }

            runOnUiThread(this::finish);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
