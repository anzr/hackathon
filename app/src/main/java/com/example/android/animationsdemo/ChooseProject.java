package com.example.android.animationsdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by t-anmo on 7/26/2015.
 */

//NO NEED TO FETCH HARDCODED
public class ChooseProject extends Activity {
    private ArrayList<String> projectArray = new ArrayList<>();
    private ArrayList<String> teamArray = new ArrayList<>();
    private OAuth2Helper oAuth2Helper;
    private String PROJECT_NAME;
    private String TEAM_NAME;
    private String BOARD_NAME;
    private String templateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_project_layout);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oAuth2Helper = new OAuth2Helper(prefs);
        new getProjects().execute();
        Button btnGo= (Button) findViewById(R.id.goButton);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.PROJECT=PROJECT_NAME;
                Constants.TEAM=TEAM_NAME;
                Intent mainActivity=new Intent(ChooseProject.this,MainActivity.class);
                //mainActivity.putExtra("context", (Parcelable) OAuthAccessTokenActivity.this);
                startActivity(mainActivity);
            }
        });
    }


    private class getProjects extends AsyncTask<Void, String, Void> {
        private ProgressDialog pd;

        @Override
        protected Void doInBackground(Void... voids) {
            String getProjectsUrl = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/projects?api-version=1.0";
            try {
                String response = oAuth2Helper.executeApiGetCall(getProjectsUrl);
                JSONObject rootObj = new JSONObject(response);
                JSONArray value = rootObj.getJSONArray("value");
                for (int i = 0; i < value.length(); i++) {
                    projectArray.add(value.getJSONObject(i).getString("name"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ChooseProject.this);
            pd.setMessage("Fetching your projects...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null)
                pd.dismiss();
            setSpinnerProjectList();
        }
    }

    private void setSpinnerProjectList() {
        final ArrayAdapter<String> projectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectArray);
        final Spinner spinnerChooseProject = (Spinner) findViewById(R.id.spinnerChooseProject);
        spinnerChooseProject.setAdapter(projectAdapter);
        spinnerChooseProject.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinnerChooseProject.getSelectedItemPosition();
                        //Toast.makeText(Constants.CONTEXT, "You have selected " + Constants.BOARD_OB.getMembers().get(position), Toast.LENGTH_LONG).show();
                        // TODO Auto-generated method stub
                        PROJECT_NAME = projectArray.get(position);
                        new getTeamsAndTemplate(projectArray.get(position)).execute();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                        // TODO Auto-generated method stub

                    }

                }
        );
    }

    private class getTeamsAndTemplate extends AsyncTask<Void, String, Void> {
        private ProgressDialog pd;

        private String projectName;
        private String templateResponse;

        public getTeamsAndTemplate(String projectName) {

            this.projectName = projectName;

            teamArray.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String getTeamsUrl = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/projects/"+projectName+"/teams/?api-version=1.0";
            String templateResponseUrl=Constants.ACCOUNT_URL+"/DefaultCollection/_apis/projects/"+projectName+"?includeCapabilities=true&api-version=1.0";

            try {
                String response = oAuth2Helper.executeApiGetCall(getTeamsUrl);
                templateResponse = oAuth2Helper.executeApiGetCall(templateResponseUrl);
                JSONObject rootObj = new JSONObject(response);
                JSONArray value = rootObj.getJSONArray("value");
                for (int i = 0; i < value.length(); i++) {
                    teamArray.add(value.getJSONObject(i).getString("name"));
                }
                rootObj= new JSONObject(templateResponse);
                templateName=rootObj.getJSONObject("capabilities").getJSONObject("processTemplate").getString("templateName");
                switch (templateName){
                    case "Agile" : BOARD_NAME="";
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ChooseProject.this);
            pd.setMessage("Fetching your teams...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null)
                pd.dismiss();
            setSpinnerTeamList();
        }
    }

    private void setSpinnerTeamList() {
        final ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamArray);
        final Spinner spinnerChooseProject = (Spinner) findViewById(R.id.spinnerChooseTeam);
        spinnerChooseProject.setAdapter(teamAdapter);
        spinnerChooseProject.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinnerChooseProject.getSelectedItemPosition();
                        //Toast.makeText(Constants.CONTEXT, "You have selected " + Constants.BOARD_OB.getMembers().get(position), Toast.LENGTH_LONG).show();
                        // TODO Auto-generated method stub
                        TEAM_NAME = teamArray.get(position);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                        // TODO Auto-generated method stub

                    }

                }
        );
    }
}
