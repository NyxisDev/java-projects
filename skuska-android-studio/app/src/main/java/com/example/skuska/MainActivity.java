package com.example.skuska;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private MealApiService apiService;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> spinnerAdapter;
    private final List<Meal> meals = new ArrayList<>();
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private String selectedType = null;
    private boolean sortAsc = true;
    private Button btnSort, ingEdlo;
    private Spinner spinnerTyp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ListView listView = findViewById(R.id.mainList);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Meal meal = meals.get(position);
            Intent intent = new Intent(this, IngredientsActivity.class);
            intent.putExtra("meal_id", meal.getId());
            intent.putExtra("meal_name", meal.getNazov());
            startActivity(intent);
        });

        spinnerTyp = findViewById(R.id.spinnerTyp);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTyp.setAdapter(spinnerAdapter);
        spinnerTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String item = spinnerAdapter.getItem(position);
                selectedType = (position == 0) ? null : item;
                loadMealsFromDatabase();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSort = findViewById(R.id.btnSort);
        btnSort.setOnClickListener(v -> {
            sortAsc = !sortAsc;
            btnSort.setText(sortAsc ? "A→Z" : "Z→A");
            loadMealsFromDatabase();
        });

        ingEdlo = findViewById(R.id.ingEdlo);
        ingEdlo.setOnClickListener(v -> {
            Intent intent = new Intent(this, EdloByIng.class);
            startActivity(intent);
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://app.vykazy.ki.fpvai.ukf.sk/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        apiService = retrofit.create(MealApiService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSpinnerAndList();
    }

    private void refreshSpinnerAndList() {
        databaseExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<String> types = db.mealDao().getAllTypes();

            runOnUiThread(() -> {
                String previousType = selectedType;

                spinnerAdapter.clear();
                spinnerAdapter.add("Všetky");
                spinnerAdapter.addAll(types);
                spinnerAdapter.notifyDataSetChanged();

                if (previousType != null && types.contains(previousType)) {
                    spinnerTyp.setSelection(types.indexOf(previousType) + 1);
                } else {
                    selectedType = null;
                    spinnerTyp.setSelection(0);
                }
            });

            loadMealsFromDatabase();
        });
    }

    private void loadMealsFromDatabase() {
        databaseExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Meal> mealsList;

            if (selectedType == null) {
                mealsList = sortAsc ? db.mealDao().getAllMeals() : db.mealDao().getAllMealsDesc();
            } else {
                mealsList = sortAsc
                        ? db.mealDao().getMealsByTypeAsc(selectedType)
                        : db.mealDao().getMealsByTypeDesc(selectedType);
            }

            runOnUiThread(() -> {
                meals.clear();
                meals.addAll(mealsList);
                listAdapter.clear();
                for (Meal meal : mealsList) {
                    double price = db.ingredientDao().getTotalPriceForMeal(meal.getId());
                    listAdapter.add(String.format(java.util.Locale.getDefault(),
                            "%s (%s) — %.2f €", meal.getNazov(), meal.getTyp(), price));
                }
                listAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Nacitat_udaje_zo_servera) {
            loadMealsFromServer();
            return true;
        }
        if (id == R.id.Pridat_jedlo) {
            startActivity(new Intent(this, CreateMealActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMealsFromServer() {
        apiService.getRawMeals().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    databaseExecutor.execute(() -> parseAndSaveData(response.body()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void parseAndSaveData(String rawData) {
        String[] lines = rawData.split("\n");
        List<Meal> mealsList = new ArrayList<>();
        List<Ingredient> ingredientsList = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            try {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;

                int mealId = Integer.parseInt(parts[0].trim());
                String nazov = parts[1].trim();
                String typ = parts[2].trim();
                String ingrediencieStr = parts[3].trim();

                mealsList.add(new Meal(mealId, nazov, typ));

                String[] ingTokens = ingrediencieStr.split(";");
                for (String token : ingTokens) {
                    String[] details = token.split(" - ");
                    if (details.length == 3) {
                        String name = details[0].trim();
                        double price = Double.parseDouble(details[1].trim());
                        String weight = details[2].trim();
                        ingredientsList.add(new Ingredient(mealId, name, price, weight));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AppDatabase db = AppDatabase.getInstance(this);
        db.clearAllTables();
        db.runInTransaction(() -> {
            db.mealDao().insertAll(mealsList);
            db.ingredientDao().insertAll(ingredientsList);
        });

        runOnUiThread(this::refreshSpinnerAndList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
