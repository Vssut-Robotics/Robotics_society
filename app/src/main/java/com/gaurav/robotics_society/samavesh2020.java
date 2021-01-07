package com.gaurav.robotics_society;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class samavesh2020 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samavesh2020);


        //String url ="http://vssutrobotics.in";
        WebView wv=(WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setPluginState(PluginState.ON);
        wv.getSettings().setAllowFileAccess(true);
        wv.loadUrl("https://www.vssutrobotics.in/samavesh");
    }
}
