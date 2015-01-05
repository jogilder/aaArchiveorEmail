package com.example.aaArchiveorEmail;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */

public class ArchiveEmailIntentService extends android.app.IntentService{

    // ArchiveEmailIntentService does all the work
    public static final String ACTION_EMAIL = "EMAIL";
    public static final String ACTION_ARCHIVE = "ARCHIVE";


   public static final String SHARED_URL = "PARAM1";
   public static final String SHARED_TITLE = "PARAM2";

    public static void startAction(Context context,String sharedTextURL, String sharedTextTitle, Boolean isEmail )
    {
        Intent intent = new Intent(context, ArchiveEmailIntentService.class);
        if (isEmail)
        {intent.setAction(ACTION_EMAIL);}
        else
        {intent.setAction(ACTION_ARCHIVE);}

        intent.putExtra(SHARED_URL, sharedTextURL);
        intent.putExtra(SHARED_TITLE, sharedTextTitle);
        context.startService(intent);
    }
    public ArchiveEmailIntentService() {
        super("ArchiveEmailIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if (intent != null)
            {
                final String action = intent.getAction();
                final String strURL = intent.getStringExtra(SHARED_URL);
                final String strTitle = intent.getStringExtra(SHARED_TITLE);

                if (ACTION_ARCHIVE.equals(action)) {
                    handleActionArchive(strURL, strTitle);
                } else if (ACTION_EMAIL.equals(action)) {
                    handleActionEmail(strURL, strTitle);
                }
            }

        }
        catch(Exception e) { //TODO:WHAT HAPPENS IF THERE IS NO INTENT?
         }
    }

  private void handleActionArchive(String urlTitle,String url) {


      //String WEB_CATEGORY = "testAndroid";
      //String WEB_SERVER = "http://www.rsc.ukfsn.org/zxbblog/BlogWrite.php";

      SharedPreferences  preferences=getStoredPreferences();
      String restoredWebServer = preferences.getString("WEB_SERVER", null);
      String restoredWebQueryString= preferences.getString("WEB_QUERY_STRING", null);
      //assumption is that this is a post not a get if not sure

//    todo implement get
//
      boolean restoredWebPost=preferences.getBoolean("WEB_POST",true);

       //then we create the post form body or the query string
      String[] queryParameters =restoredWebQueryString.split("&");
      //break up the string so we have the different parameters
      HashMap<String, String> EXTRA_DATA = new HashMap<String, String>();
      //QUERYSTRING CURRENTLY LOOKS LIKE THIS
      // urlTitle=[TITLE]&url=[URL]&primaryCategory=joTest2&button=submit
      //BITS IN BRACKETS ARE REPLACED BELOW
      for(String qP: queryParameters)
      {
          String strKey = qP.split("=")[0];
          String strValue = qP.split("=")[1];
          //put the value of url and urlTitle in from the web page that has been archived
          if (strValue.equals("[TITLE]"))
          {strValue=urlTitle;}
          if (strValue.equals("[URL]"))
          {strValue=url;}
          EXTRA_DATA.put(strKey,strValue);

      }
//is it post or get?
      if (restoredWebPost) {
          //send it off to do the web server post, can't have inline version
          AsyncHttpPost asyncHttpPost = new AsyncHttpPost(EXTRA_DATA);
          asyncHttpPost.execute(restoredWebServer);
      }
      else
      {
          //TODO:do a get
      }

    }


    private void handleActionEmail(String strURL,String strTitle) {

        SharedPreferences  preferences=getStoredPreferences();
        String restoredEmailUser = preferences.getString("EMAIL_ADDRESS", null);

        try {

            HashMap<String, String> EXTRA_DATA = new HashMap<String, String>();
            EXTRA_DATA.put(Intent.EXTRA_SUBJECT, strTitle);
            EXTRA_DATA.put(Intent.EXTRA_TEXT, strURL);
            //TODO: make this email an admin setting
            EXTRA_DATA.put(Intent.EXTRA_EMAIL,restoredEmailUser);
            //send it off to do the email, can't have inline version
            AsyncEmail asyncEmail = new AsyncEmail(EXTRA_DATA);
            asyncEmail.execute();


        }
        catch(Exception e)
        {String str =e.getMessage();}

    }
 //keep static versions of the web server etc, can be updated using admin page
private SharedPreferences getStoredPreferences()
{
    return getSharedPreferences("Admin_Prefs", MODE_PRIVATE);
}
//asynchronous call to create email
 public class AsyncEmail extends AsyncTask<String, String, String> {
        private HashMap<String, String> mData = null;// post data

        /**
         * constructor
         */
        public AsyncEmail(HashMap<String, String> data) {
            mData = data;
        }

        /**
         * background
         */
        @Override
        protected String doInBackground(String... params) {
            byte[] result = null;
            String str = "";
            Resources res = getResources();
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                //set up EMAIL data
                emailIntent.setData(Uri.fromParts("mailto", mData.get(Intent.EXTRA_EMAIL), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, mData.get(Intent.EXTRA_SUBJECT));
                emailIntent.putExtra(Intent.EXTRA_TEXT, mData.get(Intent.EXTRA_TEXT));
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.putExtra("exit_on_sent", true);
             // need to set the flag for the chooser intent and the emailIntent
                Intent i = Intent.createChooser(emailIntent, "Send email");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    getApplication().startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    String errMessage=res.getString(R.string.email_failed_message);
                    Toast
                            .makeText(getApplication(),
                                    errMessage,
                                    Toast.LENGTH_SHORT).show();
                }

            }

            catch (Exception e) {
                str = e.getMessage();
            }
            if(str.length()==0)
            {
            str= res.getString(R.string.email_completed_message);
            }
            return str;
        }

        /**
         * on getting result
         */
        @Override
        protected void onPostExecute(String result) {
            // This is the one that says finished.
            Toast.makeText(ArchiveEmailIntentService.this,
                    result, Toast.LENGTH_LONG).show();
        }
    }

//asynchronous call to archive web page
    public class AsyncHttpPost extends AsyncTask<String, String, String> {

        private HashMap<String, String> mData = null;// post data

        /**
         * constructor
         */

        public AsyncHttpPost(HashMap<String, String> data) {
            mData = data;
        }

        @Override
        protected String doInBackground(String... params) {
            byte[] result = null;
            String str = "";
            HttpClient client = new DefaultHttpClient();
            Resources res = getResources();
            try {
                HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL
                //set up post data
                ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                Iterator<String> it = mData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
                }
               //TODO: change it here so catch the no post option error, eg if no wifi
                post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //will get error thrown if not 200 code
                if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK)
                {
                    result = EntityUtils.toByteArray(response.getEntity());
                    str = new String(result, "UTF-8");
                }
                else{
                    //have a switch statement to check all the errors?
                    str=res.getString(R.string.archive_failed_message) + statusLine.getStatusCode();

                }
            }
            catch (Exception e) {
                //make error message more user friendly?
                str=res.getString(R.string.archive_failed_message) + e.getMessage();

            }

            if(str.length()==0)
            {
                //if we get to here then we have success
                str= res.getString(R.string.archive_completed_message);
            }
                return str;
        }

        /**
         * on getting result
         */
        @Override
        protected void onPostExecute(String result) {
            // something...
            // This is the one that says finished.
            Toast.makeText(ArchiveEmailIntentService.this,
                    result, Toast.LENGTH_LONG).show();
        }
    }
}
