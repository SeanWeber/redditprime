package com.lightemittingsmew.redditreader;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Settings extends BaseActivity {
    Spinner themeSelect;
    String  themes[] = {"Reddit Silver", "Night Shift"};
    int newStyle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeSelect = (Spinner)findViewById(R.id.spinnerThemeSelect);
        ArrayAdapter<String> themeAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, themes);
        themeAdaptor.setDropDownViewResource(R.layout.spinner_dropdown_item);
        themeSelect.setAdapter(themeAdaptor);
        themeSelect.setOnItemSelectedListener(new ThemeSelectedListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTheme(View view){
        if(newStyle != 0){
            VolleyRequest.style = newStyle;
        }

        // The recreate method is only available in API 11 or greater
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            recreate();
        }
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
