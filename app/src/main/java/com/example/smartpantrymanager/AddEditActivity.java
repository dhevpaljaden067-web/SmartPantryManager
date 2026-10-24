
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private EditText etIngredientName, etQuantity, etUnit;
    private Button btnSaveIngredient;
    private DatabaseHelper databaseHelper;

    private boolean isEditMode = false;
    private String originalName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        databaseHelper = new DatabaseHelper(this);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        // Check whether we are editing an ingredient
        isEditMode = getIntent().getBooleanExtra(
                "EDIT_MODE", false
        );

        if (isEditMode) {
            originalName = getIntent().getStringExtra("ITEM_NAME");

            if (originalName == null) {
                originalName = "";
            }

            etIngredientName.setText(originalName);
            etQuantity.setText(
                    getIntent().getStringExtra("ITEM_QTY")
            );
            etUnit.setText(
                    getIntent().getStringExtra("ITEM_UNIT")
            );

            btnSaveIngredient.setText("Update Pantry");
        }

        btnSaveIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIngredient();
            }
        });
    }

    private void saveIngredient() {

        String name = etIngredientName.getText()
                .toString().trim();

        String qtyStr = etQuantity.getText()
                .toString().trim();

        String unit = etUnit.getText()
                .toString().trim();

        // Check ingredient name
        if (name.isEmpty()) {
            etIngredientName.setError("Enter an ingredient name");
            etIngredientName.requestFocus();
            return;
        }

        // Prevent delimiter conflicts in the pantry list
        if (name.contains(" — ")) {
            etIngredientName.setError("Remove the dash separator");
            etIngredientName.requestFocus();
            return;
        }

        // Check quantity field
        if (qtyStr.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        // Check measurement unit
        if (unit.isEmpty()) {
            etUnit.setError("Enter a measurement unit");
            etUnit.requestFocus();
            return;
        }

        double qty;

        // Check whether quantity is a valid number
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        // Quantity must be greater than zero
        if (Double.isNaN(qty) ||
                Double.isInfinite(qty) || qty <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than zero"
            );
            etQuantity.requestFocus();
            return;
        }

        // Check for duplicate ingredient names
        if (ingredientExists(name)) {
            etIngredientName.setError(
                    "This ingredient already exists"
            );
            etIngredientName.requestFocus();
            return;
        }

        boolean saved;

        // Save or update ingredient
        if (isEditMode) {
            saved = databaseHelper.updatePantryItem(
                    originalName, name, qty, unit
            );
        } else {
            saved = databaseHelper.addPantryItem(
                    name, qty, unit
            );
        }

        if (saved) {
            Toast.makeText(
                    this,
                    "Ingredient saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {
            Toast.makeText(
                    this,
                    "Unable to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Check whether the ingredient already exists
    private boolean ingredientExists(String name) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = "LOWER(TRIM(" +
                DatabaseHelper.COL_NAME + ")) = ?";

        String[] args = {
                name.toLowerCase(Locale.ROOT)
        };

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                new String[]{DatabaseHelper.COL_NAME},
                selection,
                args,
                null,
                null,
                null
        )) {

            while (cursor.moveToNext()) {

                String existingName = cursor.getString(0);

                // Allow editing the same ingredient
                if (isEditMode &&
                        existingName.trim().equalsIgnoreCase(
                                originalName.trim()
                        )) {
                    continue;
                }

                return true;
            }
        }

        return false;
    }
}
