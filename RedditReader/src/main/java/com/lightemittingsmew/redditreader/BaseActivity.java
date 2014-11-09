package com.lightemittingsmew.redditreader;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BaseActivity extends ActionBarActivity {
    static final int None = 0;
    int style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        style = VolleyRequest.style;
        setTheme(style);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        //Check if the user selected a new style
        if( style != VolleyRequest.style ){
            recreate();
        }
        super.onResume();
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem login = menu.findItem(R.id.action_login);
        MenuItem user = menu.findItem(R.id.action_user);
        MenuItem newMessage = menu.findItem(R.id.action_new_message);
        MenuItem inbox = menu.findItem(R.id.action_message);

        if(VolleyRequest.cookie == null || VolleyRequest.cookie.equals("")){
            user.setVisible(false);
            logout.setVisible(false);
            inbox.setVisible(false);

            login.setVisible(true);
        } else {
            user.setVisible(true);
            logout.setVisible(true);
            inbox.setVisible(true);

            login.setVisible(false);

            // Set the title the the username of the logged in user
            user.setTitle(VolleyRequest.user);
        }

        if(VolleyRequest.hasNewMessage){
            newMessage.setVisible(true);
        } else {
            newMessage.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:{
                Intent intent = new Intent(this, Settings.class);
                this.startActivity(intent);
                break;
            }
            case R.id.action_logout:{
                VolleyRequest.logout(this);
                finish();
                startActivity(getIntent());
                break;
            }
            case R.id.action_login:{
                Intent intent = new Intent(this, Login.class);
                this.startActivity(intent);
                break;
            }
            case R.id.action_user:{
                Intent intent = new Intent(this, User.class);
                this.startActivity(intent);
                break;
            }
            case R.id.action_new_message:{
                Intent intent = new Intent(this, Inbox.class);
                intent.putExtra(Inbox.NEW_MESSAGE, "true");
                this.startActivity(intent);
                break;
            }
            case R.id.action_message:{
                Intent intent = new Intent(this, Inbox.class);
                this.startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
