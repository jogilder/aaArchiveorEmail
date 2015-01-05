package com.example.aaArchiveorEmail;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class ShareActivityViaEmail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // TODO: No error handling
        String final_message;
        try {

            Intent sourceIntent = getIntent();
            Bundle b = new Bundle();
            b = sourceIntent.getExtras();
            String sharedTextURL = b.getString("android.intent.extra.TEXT");
            String sharedTextTitle = b.getString("android.intent.extra.SUBJECT");

            ArchiveEmailIntentService.startAction(this, sharedTextURL, sharedTextTitle, true);

        }
        catch (Exception e)
        {
            final_message=e.getMessage();
            Toast.makeText(this, final_message, Toast.LENGTH_LONG).show();
        }


        finish();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_activity2, menu);
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
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
