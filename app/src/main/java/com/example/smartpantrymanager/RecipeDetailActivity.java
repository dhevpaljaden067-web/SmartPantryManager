package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvDetailRecipeName, tvDetailIngredients, tvDetailSteps;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        tvDetailRecipeName = findViewById(R.id.tvDetailRecipeName);
        tvDetailIngredients = findViewById(R.id.tvDetailIngredients);
        tvDetailSteps = findViewById(R.id.tvDetailSteps);

        // Get recipe name passed from SuggestedRecipesActivity via Intent
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");

        if (recipeName != null) {
            loadRecipeDetails(recipeName);
        }
    }

    private void loadRecipeDetails(String name) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COL_RECIPE_INGREDIENTS + ", " +
                DatabaseHelper.COL_RECIPE_STEPS + " FROM " + DatabaseHelper.TABLE_RECIPES +
                " WHERE " + DatabaseHelper.COL_RECIPE_NAME + " = ?", new String[]{name});

        if (cursor.moveToFirst()) {
            String ingredients = cursor.getString(0).replace(",", "\n"); // Make list look clean line-by-line
            String steps = cursor.getString(1);

            tvDetailRecipeName.setText(name);
            tvDetailIngredients.setText(ingredients);
            tvDetailSteps.setText(steps);
        }
        cursor.close();
        db.close();
    }
}