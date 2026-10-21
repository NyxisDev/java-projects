package com.example.skuska;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class EdloByIng extends AppCompatActivity {
    private ListView edloList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edlo_by_ing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edloList = findViewById(R.id.edloList);
        List<String> edloListItems = new ArrayList<>();
        edloList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, edloListItems));
        AppDatabase db = AppDatabase.getInstance(this);
        for (int mealId : db.ingredientDao().getMealIdByName("mrkva")){
            for (String nameOfMeal : db.mealDao().getNameById(mealId)) {
                edloListItems.add(nameOfMeal);
            }
        }
    }
}