package com.example.android.animationsdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
//import android.support.v13.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class TokenGeneratorMainActivity extends Activity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private static final String TOKEN_SERVER_URL = "https://app.vssps.visualstudio.com/oauth2/token";
    private static final String AUTHORIZATION_SERVER_URL = "https://app.vssps.visualstudio.com/oauth2/authorize";

    /**
     * Value of the "API Key".
     */
    public static final String API_KEY = "E87303F1-E66A-43D3-BA70-F48533FF6267";

    /**
     * Value of the "API Secret".
     */
    public static final String API_SECRET = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im9PdmN6NU1fN3AtSGpJS2xGWHo5M3VfVjBabyJ9.eyJjaWQiOiJlODczMDNmMS1lNjZhLTQzZDMtYmE3MC1mNDg1MzNmZjYyNjciLCJjc2kiOiI0OGZkN2E3MC0wYjMzLTRmOGItYjFiNy0yMDI3MTkxMzg3ZDMiLCJuYW1laWQiOiJjZGI4OGNiYy00N2IwLTRlZDctYTI2Mi1lMTBjZWQ1OTUzNzQiLCJpc3MiOiJhcHAudnNzcHMudmlzdWFsc3R1ZGlvLmNvbSIsImF1ZCI6ImFwcC52c3Nwcy52aXN1YWxzdHVkaW8uY29tIiwibmJmIjoxNDM3NDAzNDk4LCJleHAiOjE0Njg5Mzk0OTh9.AY7OCtZhQY72adOmqYg0_bRna_JucxqLEQs6gOrdUl4wZecChjrQpAYi6YvwQ-pOm_jLmlYzE4pB_TJUQKvMa_vZy_R76Vfostp-D7nmXTGv0RwjeChTSNhqzbCMLxVcOWXIaQwne4ZKbAjqjaGDEHhvh08LJyLQjhJmuINB9bw_edjiyV9o76VAwmK_meFHL9F81sRuXYLx1z2oxctf9YyyNae1hKkiw8nHRjWBz1zQZeA06mgv3QWz2xA2N-GX2HgF5cxpnKnXSSHEEFu-LehjPlET0jnSuJlZBert88jHCXtnqHadxPhCw9Y5NmM6jTDWkxDAbc4RVfumHmMYjA";

    private static final String SCOPE = "vso.work_write";

    private SharedPreferences prefs;
    private TextView mTxtApiResponse;
    private OAuth2Helper oAuth2Helper;
    private Button mConnect;
    private Button mQuery;
    private TextView mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "it came here only first time");
        setContentView(R.layout.fragment_main);
        mURL = (TextView) findViewById(R.id.editText);
        mConnect = (Button) findViewById(R.id.buttonConnect);
        mTxtApiResponse = (TextView) findViewById(R.id.responseView);
        mQuery = (Button) findViewById(R.id.buttonQuery);


        mConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Connect to VSO
                String url = mURL.getText().toString();
                if (url.length() == 0) {

                    //Toast.makeText(getApplicationContext(), "Please enter your account url", Toast.LENGTH_SHORT).show();

                } else {
                    Oauth2Params.VSO_OAUTH2.setClientId(API_KEY);
                    Oauth2Params.VSO_OAUTH2.setClientSecret(API_SECRET);
                    Oauth2Params.VSO_OAUTH2.setScope(SCOPE);
                    Oauth2Params.VSO_OAUTH2.setApiUrl(url);
                    startOauthFlow(Oauth2Params.VSO_OAUTH2);
                    prefs = PreferenceManager.getDefaultSharedPreferences(TokenGeneratorMainActivity.this);
                    oAuth2Helper = new OAuth2Helper(prefs);
                }

            }
        });

        mQuery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Connect to VSO
                String url = mURL.getText().toString();
                if (url.length() == 0)
                    url = "https://anmostart.visualstudio.com/DefaultCollection/_apis/wit/workitems?ids=1,2,3&api-version=1.0";
                Oauth2Params.VSO_OAUTH2.setApiUrl(url);

            }
        });
    }


    private void startOauthFlow(Oauth2Params oauth2Params) {
        Constants.OAUTH2PARAMS = oauth2Params;
        Intent intent = new Intent(this, OAuthAccessTokenActivity.class);
        startActivity(intent);
    }

}

