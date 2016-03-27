package com.kiitanakala.superizer20;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



//import org.json.JSONObject;
//import com.aylien.textapi.TextAPIClient;
//import com.aylien.textapi.parameters.SummarizeParams;
//import com.aylien.textapi.responses.Summarize;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView summarizedText;
    EditText URLref;
    String link= "";
    private static final String TAG = "com.superizer.superizer";

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    // Text API (AYLIEN) Summarizer used for text analyzation. It is used in a seperate thread
    // because Android SDK of 22 and higher will not allow for network operations in the main
    // thread or else it will throw a android.os.NetworkOnMainThreadException
    // the AsyncTask abstract class allows for these network operations to be possible
    // (http://stackoverflow.com/questions/6343166/how-to-fix-android-os-networkonmainthreadexception)
    //                           <Params, Progress, Result>
    class Summarizer extends AsyncTask<String, Void, String> {
        String summer="";
        // the AsyncTask needs you to override doInBackground(Params... params) for you use it
        @Override
        protected String doInBackground(String... urls) {


                OkHttpClient client = new OkHttpClient();
            String postBody = "https://textanalysis-text-summarization.p.mashape.com/text-summarizer";

                Request request = new Request.Builder().post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                        .header("X-Mashape-Key", "rprHAgebeEmshOsHw3pu3foo2JMep1EHd0EjsnH1az5MLf29MC")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .url(urls[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();


                } catch (IOException e){
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
        URLref = (EditText) findViewById(R.id.URLref);

        // this enables the app to carry over the URL into the Main2Activity screen
        Intent nextScreen = getIntent();
        Bundle b = nextScreen.getExtras();
        link = nextScreen.getStringExtra("URL");
        URLref.setText(link);


        URLref.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean  handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){

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

                Intent nextScreen = new Intent(getApplicationContext(), Main2Activity.class);
                nextScreen.putExtra("URL", URLref.getText().toString());

                startActivity(nextScreen);
            }
        } );


        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                summarizedText.setText(new Summarizer().doInBackground(URLref.getText().toString()));

            }
        });

        Button summary = (Button) findViewById(R.id.summary);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;

            }
        } );

    }

}
