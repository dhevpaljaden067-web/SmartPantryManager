package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private ListView listViewSuggestedRecipes;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> matchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        listViewSuggestedRecipes = findViewById(R.id.listViewSuggestedRecipes);

        loadSuggestedRecipes();

        // Handle clicks on suggested recipes to open the detail view
        listViewSuggestedRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedRecipe = matchingRecipes.get(position);

                // Ignore if it's just the empty state placeholder message
                if (selectedRecipe.startsWith("No recipes match")) {
                    return;
                }

                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("RECIPE_NAME", selectedRecipe);
                startActivity(intent);
            }
        });
    }

    private void loadSuggestedRecipes() {
        // Fetch strictly matched recipes from database
        matchingRecipes = databaseHelper.getStrictlyMatchedRecipes();

        // Handle empty state if no recipes match the current pantry items
        if (matchingRecipes.isEmpty()) {
            matchingRecipes.add("No recipes match your pantry yet - add more ingredients!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchingRecipes);
        listViewSuggestedRecipes.setAdapter(adapter);
    }
}