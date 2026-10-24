# Smart Pantry Manager

## 1. Project Description

Smart Pantry Manager is an Android application developed in Java for Mobile App Development 700.

The application helps users reduce food waste by managing ingredients available in their pantry and suggesting recipes they can prepare without purchasing additional ingredients.

The main feature is strict recipe matching. A recipe is suggested only when the user's pantry contains every required ingredient in sufficient quantities.

## 2. Features

### Pantry Management
- Add new pantry ingredients.
- View all saved ingredients.
- Edit ingredient names, quantities and units.
- Delete ingredients that are no longer available.
- Save pantry information locally using SQLite.

### Recipe Suggestions
- Database containing 20 preloaded recipes.
- Suggest recipes based on ingredients currently available.
- Check ingredient quantities before suggesting recipes.
- Support basic unit conversions, such as kilograms to grams.
- Display a message when no recipes match the pantry.
- View complete recipe ingredients and preparation instructions.

### Input Validation
- Prevent empty ingredient names.
- Reject zero or negative quantities.
- Prevent duplicate ingredient names.
- Require measurement units.
- Display appropriate validation messages.

### Settings
- Save the preferred measurement system (Metric or Imperial).
- Save the expiring-soon alerts preference.
- Remember saved settings when the application is restarted.

The settings currently save user preferences; automatic expiry notifications and display-unit conversion are not implemented.

### Navigation
- Bottom navigation with Pantry, Recipes and Settings.
- Navigation between screens using Android Intents.
- Custom ListView Adapter for displaying pantry ingredients.

## 3. Technologies Used

- **Programming Language:** Java
- **Development Environment:** Android Studio
- **Database:** SQLite
- **User Interface:** XML layouts
- **Data Display:** ListView with custom ArrayAdapter
- **Local Preferences:** SharedPreferences
- **Version Control:** Git and GitHub

## 4. Database Implementation

SQLite was selected because Smart Pantry Manager requires a simple local database that works without an internet connection.

The database stores pantry ingredients, recipes and their ingredient requirements.

### Database Tables

**pantry**
- Stores ingredient names, available quantities and measurement units.

**recipes**
- Stores recipe names, ingredient descriptions and preparation steps.

**recipe_ingredients**
- Stores the ingredients, required quantities and measurement units for each recipe.

SQLiteOpenHelper is used to manage the database.

## 5. Strict Recipe Matching

The application compares the ingredients required by each recipe with the ingredients stored in the user's pantry.

A recipe is suggested only when:

1. Every required ingredient exists in the pantry.
2. The available quantity is sufficient.
3. Measurement units are compatible.

For example, if Garlic Fried Rice requires 150 g of rice and the user has only 100 g, the recipe will not appear.

If the user has 200 g of rice and all other required ingredients, the recipe can appear in the suggestions.

## 6. Application Screens

1. **Pantry Screen:** Displays saved pantry ingredients.
2. **Add/Edit Ingredient Screen:** Allows users to manage ingredients.
3. **Suggested Recipes Screen:** Displays recipes that match the pantry.
4. **Recipe Detail Screen:** Displays ingredients and preparation instructions.
5. **Settings Screen:** Allows users to save preferences.

## 7. How to Run the Application

1. Download or clone this GitHub repository.
2. Open Android Studio.
3. Select **File → Open**.
4. Select the SmartPantryManager project folder.
5. Allow Gradle to sync.
6. Start an Android emulator or connect a physical Android device with USB debugging enabled.
7. Select the device in Android Studio.
8. Run the application.

The application has been tested using a Pixel 4a Android emulator running Android 11 (API 30).

## 8. Testing

The application has been manually tested for:

- Adding, editing and deleting pantry ingredients.
- Saving ingredients after restarting the application.
- Preventing duplicate ingredients.
- Rejecting zero quantities and empty fields.
- Displaying suggested recipes.
- Removing recipes from suggestions when ingredient quantities are insufficient.
- Converting compatible measurement units during matching.
- Displaying recipe details.
- Saving settings between application sessions.
- Navigating between application screens.

## 9. Future Improvements

Possible future improvements include:

- Expiry date tracking and automatic expiry notifications.
- Automatic conversion of displayed quantities between measurement systems.
- Improved ingredient name normalization.
- More recipe choices.
- Additional user interface improvements.

## 10. Conclusion

Smart Pantry Manager demonstrates Android development using Java, SQLite, Activities, Intents, XML layouts, custom Adapters and persistent data storage.

The project focuses on reducing food waste by suggesting only recipes that can be prepared using ingredients already available in the user's pantry.
