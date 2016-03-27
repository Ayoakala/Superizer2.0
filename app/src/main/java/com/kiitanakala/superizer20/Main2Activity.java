package com.kiitanakala.superizer20;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    EditText URL;
    String link = "";
    private WebView webView;
    private static final String TAG = "com.superizer.superizer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        Log.i(TAG, "onCreate");

        URL = (EditText) findViewById(R.id.URLref);
        // this enables the app to carry over the URL into the MainActivity screen
        Intent nextScreen = getIntent();
        Bundle b = nextScreen.getExtras();
        link = nextScreen.getStringExtra("URL");
        URL.setText(link);


        // allows user to input a URL
        URL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    link = v.getText().toString();
                    Toast.makeText(Main2Activity.this, link, Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                handled = true;
                return handled;
            }
        });
        Intent i = getIntent();

        String link = i.getStringExtra("URL");

        //this enables me to run the internet search without opening the generic browser
        //(https://www.youtube.com/watch?v=khWoiDDb2Dw)
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new ActivityWebClient());
        webView.loadUrl("https://www.google.com");
        webView.loadUrl("https://" + URL.getText().toString());
        if(URL.getText().toString().equals("")){
            webView.loadUrl("https://www.google.com");
        }

        Button summary = (Button) findViewById(R.id.summary);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                nextScreen.putExtra("URL", URL.getText().toString());
                startActivity(nextScreen);
            }
        });

        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl(URL.getText().toString());

            }

        });

        Button original = (Button) findViewById(R.id.original);
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }
    // this works in unison with the Webview which allows it to do searches within the app without
    // utilizing the generic android browser to carry out the command
    private class ActivityWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView webView, String url){
            webView.loadUrl(url);
            return true;
        }
    }
    // this works in unison with the Webview which allows it to go back within the app without
    // utilizing the generic android browser to carry out the command
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack() ){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


}
