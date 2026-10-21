package com.example.skuska;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientsActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int mealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_ingredients);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mealId = getIntent().getIntExtra("meal_id", -1);
        String mealName = getIntent().getStringExtra("meal_name");
        setTitle(mealName);

        ListView listView = findViewById(R.id.ingredientsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        executor.execute(() -> {
            List<Ingredient> ingredients = AppDatabase.getInstance(this)
                    .ingredientDao()
                    .getIngredientsForMeal(mealId);
            runOnUiThread(() -> {
                for (Ingredient ing : ingredients) {
                    adapter.add(String.format(java.util.Locale.getDefault(),
                            "%s — %s — %.2f €", ing.getNazov_ing(), ing.getHmotnost(), ing.getCena()));
                }
            });
        });

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateMealActivity.class);
            intent.putExtra("meal_id", mealId);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnDelete).setOnClickListener(v ->
            new AlertDialog.Builder(this)
                .setTitle("Vymazať jedlo")
                .setMessage("Naozaj chcete vymazať toto jedlo?")
                .setPositiveButton("Áno", (dialog, which) -> {
                    executor.execute(() -> {
                        AppDatabase.getInstance(this).mealDao().deleteById(mealId);
                        runOnUiThread(this::finish);
                    });
                })
                .setNegativeButton("Nie", null)
                .show()
        );
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
