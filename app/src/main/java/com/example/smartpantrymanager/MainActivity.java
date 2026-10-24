
package com.example.smartpantrymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView pantryListView;
    private FloatingActionButton fabAdd;
    private Button btnOpenSuggestedRecipes, btnOpenSettings;
    private BottomNavigationView bottomNavigation;

    private DatabaseHelper databaseHelper;
    private ArrayList<String> pantryItemList;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Connect XML components
        pantryListView = findViewById(R.id.listViewPantry);
        fabAdd = findViewById(R.id.fabAdd);

        btnOpenSuggestedRecipes = findViewById(
                R.id.btnOpenSuggestedRecipes
        );

        btnOpenSettings = findViewById(
                R.id.btnOpenSettings
        );

        bottomNavigation = findViewById(
                R.id.bottomNavigation
        );

        // Add new ingredient
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        MainActivity.this,
                        AddEditActivity.class
                );

                startActivity(intent);
            }
        });

        // Open Suggested Recipes using top button
        btnOpenSuggestedRecipes.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                SuggestedRecipesActivity.class
                        );

                        startActivity(intent);
                    }
                });

        // Open Settings using top button
        btnOpenSettings.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        );

                        startActivity(intent);
                    }
                });

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navPantry) {

                // Already on Pantry screen
                return true;

            } else if (itemId == R.id.navRecipes) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SuggestedRecipesActivity.class
                );

                startActivity(intent);
                return true;

            } else if (itemId == R.id.navSettings) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);
                return true;
            }

            return false;
        });

        // Handle pantry item clicks
        pantryListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String selectedItem = pantryItemList.get(position);

                        // Ignore empty pantry message
                        if (selectedItem.startsWith(
                                "Your pantry is currently empty")) {
                            return;
                        }

                        // Separate ingredient name and quantity
                        String[] parts = selectedItem.split(" — ", 2);

                        if (parts.length < 2) {
                            return;
                        }

                        String ingredientName = parts[0];

                        String[] qtyUnitParts =
                                parts[1].trim().split("\\s+", 2);

                        if (qtyUnitParts.length < 2) {
                            return;
                        }

                        String quantity = qtyUnitParts[0];
                        String unit = qtyUnitParts[1];

                        // Display Edit and Delete options
                        CharSequence[] options = {
                                "Edit Item",
                                "Delete Item"
                        };

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Manage " + ingredientName)
                                .setItems(options,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {

                                                if (which == 0) {

                                                    // Edit ingredient
                                                    Intent intent = new Intent(
                                                            MainActivity.this,
                                                            AddEditActivity.class
                                                    );

                                                    intent.putExtra(
                                                            "EDIT_MODE", true
                                                    );

                                                    intent.putExtra(
                                                            "ITEM_NAME", ingredientName
                                                    );

                                                    intent.putExtra(
                                                            "ITEM_QTY", quantity
                                                    );

                                                    intent.putExtra(
                                                            "ITEM_UNIT", unit
                                                    );

                                                    startActivity(intent);

                                                } else {

                                                    // Confirm deletion
                                                    new AlertDialog.Builder(
                                                            MainActivity.this
                                                    )
                                                            .setTitle("Remove Item")
                                                            .setMessage(
                                                                    "Do you want to remove "
                                                                            + ingredientName
                                                                            + " from your pantry?"
                                                            )
                                                            .setPositiveButton(
                                                                    "Yes",
                                                                    new DialogInterface.OnClickListener() {

                                                                        @Override
                                                                        public void onClick(
                                                                                DialogInterface dialog,
                                                                                int whichBtn) {

                                                                            databaseHelper
                                                                                    .deletePantryItem(
                                                                                            ingredientName
                                                                                    );

                                                                            loadPantryDataFromDB();
                                                                        }
                                                                    })
                                                            .setNegativeButton(
                                                                    "Cancel", null
                                                            )
                                                            .show();
                                                }
                                            }
                                        })
                                .show();
                    }
                });
    }

    // Refresh pantry and reset navigation
    @Override
    protected void onResume() {
        super.onResume();

        // Load latest ingredients
        loadPantryDataFromDB();

        // Always highlight Pantry on this screen
        bottomNavigation.setSelectedItemId(
                R.id.navPantry
        );
    }

    // Load pantry ingredients from SQLite
    private void loadPantryDataFromDB() {

        pantryItemList = new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_PANTRY,
                null
        );

        if (cursor.moveToFirst()) {

            do {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_NAME
                        )
                );

                double qty = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_QTY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_UNIT
                        )
                );

                // Prepare ingredient for display
                String displayString =
                        name + " — " + qty + " " + unit;

                pantryItemList.add(displayString);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Show message if pantry is empty
        if (pantryItemList.isEmpty()) {

            pantryItemList.add(
                    "Your pantry is currently empty. Tap '+' to add items!"
            );
        }

        // Display ingredients using custom Adapter
        adapter = new PantryAdapter(
                this,
                pantryItemList
        );

        pantryListView.setAdapter(adapter);
    }
}
