
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private RadioButton radioMetric, radioImperial;
    private Button btnSaveSettings;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchNotifications = findViewById(R.id.switchNotifications);
        radioMetric = findViewById(R.id.radioMetric);
        radioImperial = findViewById(R.id.radioImperial);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Open saved preferences
        preferences = getSharedPreferences(
                "pantry_settings",
                MODE_PRIVATE
        );

        // Load notification setting
        boolean notificationsEnabled = preferences.getBoolean(
                "notifications_enabled",
                false
        );

        switchNotifications.setChecked(notificationsEnabled);

        // Load measurement setting
        boolean useImperial = preferences.getBoolean(
                "use_imperial",
                false
        );

        if (useImperial) {
            radioImperial.setChecked(true);
        } else {
            radioMetric.setChecked(true);
        }

        // Save both settings
        btnSaveSettings.setOnClickListener(v -> {

            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(
                    "notifications_enabled",
                    switchNotifications.isChecked()
            );

            editor.putBoolean(
                    "use_imperial",
                    radioImperial.isChecked()
            );

            editor.apply();

            Toast.makeText(
                    SettingsActivity.this,
                    "Settings saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }
}
