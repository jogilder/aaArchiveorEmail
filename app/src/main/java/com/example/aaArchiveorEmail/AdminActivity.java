package com.example.aaArchiveorEmail;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.content.SharedPreferences;
import android.widget.RadioButton;
import android.widget.Toast;


public class AdminActivity extends ActionBarActivity {
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            //TODO: need to fill in values for the web in onActivityCreated later on as they aren't created until later

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Called when the user clicks the Update button
     */
    public void adminUpdate(View view) {
        // Do something in response to UPDATE button
        //TODO: CHECK THAT INPUTS ARE VALID URLS AND EMAIL ADDRESSES
        EditText editWebServer = (EditText) findViewById(R.id.admin_webserver);
        EditText editWebQueryString = (EditText) findViewById(R.id.admin_querystring);
        EditText editEmailAddress = (EditText) findViewById(R.id.admin_email);
        RadioButton editRadioGet = (RadioButton) findViewById(R.id.admin_get_radio);
        RadioButton editRadioPost = (RadioButton) findViewById(R.id.admin_post_radio);
        String newUrl = editWebServer.getText().toString();
        String newQueryString = editWebQueryString.getText().toString();
        String newEmailAddress = editEmailAddress.getText().toString();
        boolean newGet = editRadioGet.isChecked();
        boolean newPost = editRadioPost.isChecked();

        //update the stored values used for web server, query string and the email address if they have values

        SharedPreferences preferencesU = getSharedPreferences("Admin_Prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesU.edit();

        if (newUrl != null) {
            editor.putString("WEB_SERVER", newUrl);
        }
        if (newQueryString != null)
            editor.putString("WEB_QUERY_STRING", newQueryString);
        if (newEmailAddress !=null){
            editor.putString("EMAIL_ADDRESS",newEmailAddress);

        }
        editor.putBoolean("WEB_GET", newGet);
        editor.putBoolean("WEB_POST", newPost);
        editor.commit();
        //display a second page showing that we have updated the details

        //TODO: if there is an error then post the error with option to update
        //just do a toast message
        Resources res = getResources();
        //if don't run main activity then have only share message created

        String message=  res.getString(R.string.ADMIN_MESSAGE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {


                View rootView = inflater.inflate(R.layout.fragment_admin, container, false);
                return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {

            super.onActivityCreated(savedInstanceState);
            //set up default values
            //get the values of the current web_server and category

            SharedPreferences preferences = this.getActivity().getSharedPreferences("Admin_Prefs", MODE_PRIVATE);
            String restoredWebServer = preferences.getString("WEB_SERVER","");
            String restoredQueryString= preferences.getString("WEB_QUERY_STRING", "");
            String restoredEmailAddress= preferences.getString("EMAIL_ADDRESS", "");
            boolean restoredCheckBoxGet=preferences.getBoolean("WEB_GET",false);
            boolean restoredCheckBoxPost=preferences.getBoolean("WEB_POST",true);

            //now put the values into the text boxes
            EditText editWebServer = (EditText) this.getActivity().findViewById(R.id.admin_webserver);
            EditText editEmailAddress = (EditText) this.getActivity().findViewById(R.id.admin_email);
            EditText editWebQueryString = (EditText) this.getActivity().findViewById(R.id.admin_querystring);
            RadioButton editRadioGet =(RadioButton)this.getActivity().findViewById(R.id.admin_get_radio);
            RadioButton editRadioPost =(RadioButton)this.getActivity().findViewById(R.id.admin_post_radio);
            editRadioGet.setChecked(restoredCheckBoxGet);
            editRadioPost.setChecked(restoredCheckBoxPost);
            editWebServer.setText(restoredWebServer);
            editWebQueryString.setText(restoredQueryString);
            editEmailAddress.setText(restoredEmailAddress);
    }

    }
}