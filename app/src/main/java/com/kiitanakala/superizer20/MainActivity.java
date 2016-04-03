package com.kiitanakala.superizer20;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//JSON import statements for parsing JSON objects and arrays
import org.json.JSONArray;
import org.json.JSONObject;

//possible input out put checked excpetion possbility
import java.io.IOException;

//OkHttp api imports
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class MainActivity extends AppCompatActivity {
    TextView summarizedText;
    EditText URLref;
    String link = "";
    private static final String TAG = "com.superizer.superizer";

    // The Mashapes media typed needs to be parsed from charset=utf-8
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

     /* Mashape Text Summarization Api (https://market.mashape.com/textanalysis/text-summarization)
     and Article Anlaysis (https://market.mashape.com/adlegant/article-analysis#)
     .It is used in a seperate thread
     because Android SDK of 22 and higher will not allow for network operations in the main
     thread or else it will throw a android.os.NetworkOnMainThreadException
     the AsyncTask abstract class allows for these network operations to be possible
     (http://stackoverflow.com/questions/6343166/how-to-fix-android-os-networkonmainthreadexception)

                                      <Params, Progress, Result>
     */
    class Summarizer extends AsyncTask<String, Void, String> {
        String summer = "";

        // the AsyncTask needs you to override doInBackground(Params... params) for you use it
        @Override
        protected String doInBackground(String... urls) {

            /*
                instead of using the generic Unirest dependency for a Mashape API I realized that
                Unirest did not work very well with android programming (when I added the library it
                was missing some paths to integral import libraries in java). I decided to use
                OKHttp which was a learning curve because Mashape was mad fro Unirest but with
                multiple references to Stack overflow posts (too many to list) I finally got it to
                work!!
             */
            OkHttpClient client = new OkHttpClient();
            String postBody = "https://textanalysis-text-summarization.p.mashape.com/text-summarizer";

            // This is the Authors api call
            Request author = new Request.Builder()
                    .url("https://joanfihu-article-analysis-v1.p.mashape.com/author?link=" + urls[0])
                    .header("X-Mashape-Key", "rprHAgebeEmshOsHw3pu3foo2JMep1EHd0EjsnH1az5MLf29MC")
                    .build();


            // cryptic syntax to access the server for the summarization, the example for this is in
            // the mashape site.
            RequestBody body = RequestBody.create(JSON, "{\"url\":\"" + urls[0] + "\",\"text\":\"\",\"sentnum\":6}");
            Request request = new Request.Builder()
                    .url("https://textanalysis-text-summarization.p.mashape.com/text-summarizer")
                    .post(body)
                    .header("X-Mashape-Key", "rprHAgebeEmshOsHw3pu3foo2JMep1EHd0EjsnH1az5MLf29MC")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
            try {
                // this gets the response from the Mashape server as a Response whcih needs to be
                // parsed to be understood
                Response authorResponse = client.newCall(author).execute();

                Response response = client.newCall(request).execute();

                // stores the response as a string
                String authorStr = authorResponse.body().string();

                String jsonSummer = response.body().string();

                try {
                    String text = "";
                    // Author JSON objects
                    JSONObject auth = new JSONObject(authorStr);
                    JSONArray arrayAuth = auth.getJSONArray("author");
                    //Summarization JSON objects
                    JSONObject jsonobj = new JSONObject(jsonSummer);
                    JSONArray array = jsonobj.getJSONArray("sentences");

                    text += "Author: " + arrayAuth.get(0).toString() + "\n\n";
                    for (int i = 0; i < 6; i++) {
                        text += array.get(i).toString() + "\n\n";
                    }
                    return text;

                    //JSONs checked exception  possiblity
                } catch (org.json.JSONException x) {
                    return "Cannot summarize, please try another article";

                }
                //IO checked exception possiblity
            } catch (IOException e) {
                return "Cannot summarize, please try another article";

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // This allows me to perform tasks no normally acceptable in the main thread
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.i(TAG, "onCreate");

        summarizedText = (TextView) findViewById(R.id.summarizedText);
        // enables the Textview to be scrollable
        summarizedText.setMovementMethod(new ScrollingMovementMethod());
        URLref = (EditText) findViewById(R.id.URLref);

        // this enables the app to carry over the URL into the Main2Activity screen
        Intent nextScreen = getIntent();
        Bundle b = nextScreen.getExtras();
        link = nextScreen.getStringExtra("URL");
        summarizedText.setText(nextScreen.getStringExtra("text"));
        URLref.setText(link);


        URLref.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String link = v.getText().toString();
                    Toast.makeText(MainActivity.this, link, Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                return handled;
            }
        });

        Button original = (Button) findViewById(R.id.original);
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this intent usage allows me to carry over my url and my summarized text to the
                // nexts screen
                Intent nextScreen = new Intent(getApplicationContext(), Main2Activity.class);
                nextScreen.putExtra("URL", URLref.getText().toString());
                nextScreen.putExtra("text", summarizedText.getText().toString());

                startActivity(nextScreen);
            }
        });


        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this calls the Asyncs doinbackgorund method which is where the Text API is used
                summarizedText.setText(new Summarizer().doInBackground(URLref.getText().toString()));
            }
        });

        // no fuctionality for this button because we are already in the summarize activity
        Button summary = (Button) findViewById(R.id.summary);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;

            }
        });


    }

}
