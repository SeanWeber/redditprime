package com.lightemittingsmew.redditreader;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

public class Settings extends BaseActivity {
    Spinner themeSelect;
    String  themes[] = {"Reddit Silver", "Night Shift"};
    int newStyle = 0;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set up the theme select spinner
        themeSelect = (Spinner)findViewById(R.id.spinnerThemeSelect);
        ArrayAdapter<String> themeAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, themes);
        themeAdaptor.setDropDownViewResource(R.layout.spinner_dropdown_item);
        themeSelect.setAdapter(themeAdaptor);

        // Set the dropdown to the current theme
        if(VolleyRequest.style == R.style.Dark){
            themeSelect.setSelection(1);
        }
        themeSelect.setOnItemSelectedListener(new ThemeSelectedListener());

        // Set this value if it was previously selected
        CheckBox colorizeBox = (CheckBox) findViewById(R.id.checkBoxColorize);
        colorizeBox.setChecked(!VolleyRequest.disableScoreColor);

        // Check the button that was previously selected
        RadioButton loadAlways = (RadioButton) findViewById(R.id.radio_load_hd_always);
        RadioButton loadWifi = (RadioButton) findViewById(R.id.radio_load_hd_wifi);
        RadioButton loadNever = (RadioButton) findViewById(R.id.radio_load_hd_never);
        switch(VolleyRequest.loadHdThumbnailSetting) {
            case VolleyRequest.AlwaysLoad:
                loadAlways.setChecked(true);
                break;
            case VolleyRequest.WifiLoad:
                loadWifi.setChecked(true);
                break;
            case VolleyRequest.NeverLoad:
                loadNever.setChecked(true);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);

        return true;
    }

    public void setTheme(View view){
        if(newStyle != 0){
            VolleyRequest.style = newStyle;

            // Save the value so it persists after the app is closed
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putInt("Style", newStyle);
            prefEditor.commit();

            // The recreate method is only available in API 11 or greater
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                recreate();
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_load_hd_always:
                if (checked){
                    VolleyRequest.loadHdThumbnailSetting = VolleyRequest.AlwaysLoad;
                }
                break;
            case R.id.radio_load_hd_wifi:
                if (checked){
                    VolleyRequest.loadHdThumbnailSetting = VolleyRequest.WifiLoad;
                }
                break;
            case R.id.radio_load_hd_never:
                if (checked){
                    VolleyRequest.loadHdThumbnailSetting = VolleyRequest.NeverLoad;
                }
                break;
        }

        // Save the value so it persists after the app is closed
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt("LoadHdThumbnailSetting", VolleyRequest.loadHdThumbnailSetting);
        prefEditor.commit();
    }

    public void onColorizeClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();
        VolleyRequest.disableScoreColor = !checked;

        // Save the value so it persists after the app is closed
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean("DisableScoreColor", VolleyRequest.disableScoreColor);
        prefEditor.commit();
    }

    class ThemeSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if(pos == 0){
                newStyle = R.style.Light;
            } else if(pos == 1){
                newStyle = R.style.Dark;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {}
    }
}
