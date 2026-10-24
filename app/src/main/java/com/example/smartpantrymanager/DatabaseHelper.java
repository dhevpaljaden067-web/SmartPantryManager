package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    // Version 4 adds recipe quantities without deleting pantry items.
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QTY = "quantity";
    public static final String COL_UNIT = "unit";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients";
    public static final String COL_RECIPE_STEPS = "steps";

    private static final String TABLE_REQUIREMENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QTY + " REAL, " +
                COL_UNIT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_RECIPE_INGREDIENTS + " TEXT, " +
                COL_RECIPE_STEPS + " TEXT)");

        createRequirementsTable(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Leave the user's pantry untouched. Only refresh the seeded recipes.
            createRequirementsTable(db);
            db.delete(TABLE_RECIPES, null, null);
            seedRecipes(db);
        }
    }

    private void createRequirementsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REQUIREMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL)");
    }

    // Each recipe's ingredient rows have a name, a required amount and a unit.
    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] needed) {
        StringBuilder ingredientDisplay = new StringBuilder();
        for (int i = 0; i < needed.length; i++) {
            if (i > 0) ingredientDisplay.append(", ");
            ingredientDisplay.append(needed[i][1]).append(" ")
                    .append(needed[i][2]).append(" ").append(needed[i][0]);
        }

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_RECIPE_INGREDIENTS, ingredientDisplay.toString());
        recipeValues.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insertOrThrow(TABLE_RECIPES, null, recipeValues);

        for (String[] item : needed) {
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("ingredient_name", item[0]);
            values.put("quantity", Double.parseDouble(item[1]));
            values.put("unit", item[2]);
            db.insertOrThrow(TABLE_REQUIREMENTS, null, values);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Boiled Pasta",
                "1. Boil pasta until tender.\n2. Drain it.\n3. Stir in tomato sauce and top with cheese.",
                new String[][]{{"pasta", "100", "g"}, {"tomato sauce", "50", "ml"}, {"cheese", "20", "g"}});

        addRecipe(db, "Simple Omelette",
                "1. Beat eggs with salt.\n2. Melt butter in a pan.\n3. Cook the eggs until set and fold.",
                new String[][]{{"egg", "2", "pcs"}, {"butter", "10", "g"}, {"salt", "1", "g"}});

        addRecipe(db, "Garlic Fried Rice",
                "1. Heat oil and fry chopped garlic.\n2. Add cooked rice.\n3. Stir in soy sauce and serve.",
                new String[][]{{"rice", "150", "g"}, {"garlic", "2", "pcs"}, {"soy sauce", "15", "ml"}, {"oil", "10", "ml"}});

        addRecipe(db, "Egg on Toast",
                "1. Toast the bread.\n2. Fry the egg in butter.\n3. Place the egg on the toast.",
                new String[][]{{"bread", "2", "pcs"}, {"egg", "1", "pcs"}, {"butter", "5", "g"}});

        addRecipe(db, "Cheese Toast",
                "1. Top bread with cheese.\n2. Toast until the cheese has melted.",
                new String[][]{{"bread", "2", "pcs"}, {"cheese", "30", "g"}});

        addRecipe(db, "Buttered Rice",
                "1. Cook the rice.\n2. Mix with butter and salt before serving.",
                new String[][]{{"rice", "150", "g"}, {"butter", "10", "g"}, {"salt", "1", "g"}});

        addRecipe(db, "Tomato Salad",
                "1. Slice the tomatoes and onion.\n2. Toss with oil and serve.",
                new String[][]{{"tomato", "2", "pcs"}, {"onion", "1", "pcs"}, {"oil", "10", "ml"}});

        addRecipe(db, "Fruit Salad",
                "1. Peel and chop the fruit.\n2. Combine in a bowl and serve.",
                new String[][]{{"banana", "1", "pcs"}, {"apple", "1", "pcs"}, {"orange", "1", "pcs"}});

        addRecipe(db, "Banana Smoothie",
                "1. Peel the banana.\n2. Blend it with milk until smooth.",
                new String[][]{{"banana", "1", "pcs"}, {"milk", "200", "ml"}});

        addRecipe(db, "Cheese Sandwich",
                "1. Spread butter on bread.\n2. Add cheese and close the sandwich.",
                new String[][]{{"bread", "2", "pcs"}, {"cheese", "30", "g"}, {"butter", "5", "g"}});

        addRecipe(db, "Scrambled Eggs",
                "1. Beat the eggs with milk.\n2. Melt butter in a pan.\n3. Stir gently until the eggs set.",
                new String[][]{{"egg", "2", "pcs"}, {"butter", "10", "g"}, {"milk", "30", "ml"}});

        addRecipe(db, "Mashed Potatoes",
                "1. Boil peeled potatoes until soft.\n2. Mash with butter and milk.\n3. Season with salt.",
                new String[][]{{"potato", "2", "pcs"}, {"butter", "20", "g"}, {"milk", "50", "ml"}, {"salt", "1", "g"}});

        addRecipe(db, "Fried Potatoes",
                "1. Cut potatoes into cubes.\n2. Fry in oil until golden.\n3. Sprinkle with salt.",
                new String[][]{{"potato", "2", "pcs"}, {"oil", "30", "ml"}, {"salt", "1", "g"}});

        addRecipe(db, "Cucumber Salad",
                "1. Chop the cucumber and tomato.\n2. Toss with oil.",
                new String[][]{{"cucumber", "1", "pcs"}, {"tomato", "1", "pcs"}, {"oil", "10", "ml"}});

        addRecipe(db, "Garlic Bread",
                "1. Mash the garlic into the butter.\n2. Spread over bread.\n3. Toast until golden.",
                new String[][]{{"bread", "2", "pcs"}, {"garlic", "1", "pcs"}, {"butter", "15", "g"}});

        addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread peanut butter on one slice of bread.\n2. Cover with the other slice.",
                new String[][]{{"bread", "2", "pcs"}, {"peanut butter", "30", "g"}});

        addRecipe(db, "Jam Toast",
                "1. Toast bread.\n2. Spread jam on top.",
                new String[][]{{"bread", "2", "pcs"}, {"jam", "20", "g"}});

        addRecipe(db, "Oat Porridge",
                "1. Heat milk in a pot.\n2. Stir in oats and simmer until thick.",
                new String[][]{{"oats", "60", "g"}, {"milk", "200", "ml"}});

        addRecipe(db, "Apple Yogurt Bowl",
                "1. Chop the apple.\n2. Mix with yogurt and serve.",
                new String[][]{{"apple", "1", "pcs"}, {"yogurt", "150", "g"}});

        addRecipe(db, "Boiled Eggs",
                "1. Place eggs in water in a pot.\n2. Boil until cooked.\n3. Peel and sprinkle with salt.",
                new String[][]{{"egg", "2", "pcs"}, {"salt", "1", "g"}});
    }

    // Convert common spelling and singular/plural variants to a shared name.
    private String normalName(String name) {
        String result = name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        if (result.equals("tomatoes")) return "tomato";
        if (result.equals("potatoes")) return "potato";
        if (result.equals("garlic cloves") || result.equals("cloves of garlic")) return "garlic";
        if (result.endsWith("ies") && result.length() > 4)
            return result.substring(0, result.length() - 3) + "y";
        if (result.endsWith("s") && !result.endsWith("ss") && result.length() > 3)
            return result.substring(0, result.length() - 1);
        return result;
    }

    // Return a common unit and amount so kg/g and L/ml can be compared.
    private String normalUnit(String unit) {
        String u = unit.toLowerCase(Locale.ROOT).trim();
        if (u.equals("kg") || u.equals("kilogram") || u.equals("kilograms")) return "g";
        if (u.equals("gram") || u.equals("grams")) return "g";
        if (u.equals("l") || u.equals("litre") || u.equals("litres") ||
                u.equals("liter") || u.equals("liters")) return "ml";
        if (u.equals("milliliter") || u.equals("milliliters") ||
                u.equals("millilitre") || u.equals("millilitres")) return "ml";
        if (u.equals("pc") || u.equals("piece") || u.equals("pieces") ||
                u.equals("unit") || u.equals("units") || u.equals("each")) return "pcs";
        return u;
    }

    private double baseQuantity(double qty, String unit) {
        String u = unit.toLowerCase(Locale.ROOT).trim();
        if (u.equals("kg") || u.equals("kilogram") || u.equals("kilograms") ||
                u.equals("l") || u.equals("litre") || u.equals("litres") ||
                u.equals("liter") || u.equals("liters")) {
            return qty * 1000;
        }
        return qty;
    }

    // True only if all recipe requirements are present in sufficient amounts.
    public ArrayList<String> getStrictlyMatchedRecipes() {
        ArrayList<String> matches = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<String> pantryNames = new ArrayList<>();
        ArrayList<String> pantryUnits = new ArrayList<>();
        ArrayList<Double> pantryAmounts = new ArrayList<>();

        Cursor pantry = db.rawQuery("SELECT name, quantity, unit FROM " + TABLE_PANTRY, null);
        while (pantry.moveToNext()) {
            pantryNames.add(normalName(pantry.getString(0)));
            pantryAmounts.add(baseQuantity(pantry.getDouble(1), pantry.getString(2)));
            pantryUnits.add(normalUnit(pantry.getString(2)));
        }
        pantry.close();

        Cursor recipes = db.rawQuery("SELECT recipe_id, recipe_name FROM " + TABLE_RECIPES, null);
        while (recipes.moveToNext()) {
            int id = recipes.getInt(0);
            boolean allAvailable = true;
            int requirementCount = 0;

            Cursor requirements = db.rawQuery(
                    "SELECT ingredient_name, quantity, unit FROM " + TABLE_REQUIREMENTS + " WHERE recipe_id = ?",
                    new String[]{String.valueOf(id)});

            while (requirements.moveToNext()) {
                requirementCount++;
                String neededName = normalName(requirements.getString(0));
                String neededUnit = normalUnit(requirements.getString(2));
                double neededQty = baseQuantity(requirements.getDouble(1), requirements.getString(2));
                double availableQty = 0;

                for (int i = 0; i < pantryNames.size(); i++) {
                    if (neededName.equals(pantryNames.get(i)) && neededUnit.equals(pantryUnits.get(i))) {
                        availableQty += pantryAmounts.get(i);
                    }
                }
                if (availableQty + 0.000001 < neededQty) {
                    allAvailable = false;
                    break;
                }
            }
            requirements.close();
            if (allAvailable && requirementCount > 0) {
                matches.add(recipes.getString(1));
            }
        }
        recipes.close();
        db.close();
        return matches;
    }

    public boolean addPantryItem(String name, double quantity, String unit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim().toLowerCase(Locale.ROOT));
        values.put(COL_QTY, quantity);
        values.put(COL_UNIT, unit.trim());
        return db.insert(TABLE_PANTRY, null, values) != -1;
    }

    public boolean updatePantryItem(String originalName, String newName, double quantity, String unit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, newName.trim().toLowerCase(Locale.ROOT));
        values.put(COL_QTY, quantity);
        values.put(COL_UNIT, unit.trim());
        return db.update(TABLE_PANTRY, values, COL_NAME + " = ?", new String[]{originalName}) > 0;
    }

    public void deletePantryItem(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_NAME + " = ?", new String[]{name});
    }
}
