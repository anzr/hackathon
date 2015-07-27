package com.example.android.animationsdemo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

//TODO: create a method to return fragments of total columns based on the queries
public class ApiCalls {
    private OAuth2Helper oAuth2Helper;

    public ApiCalls() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
        oAuth2Helper = new OAuth2Helper(prefs);
    }


    public void initiateHandleDragDropTasks(String colName, int position, ViewGroup rootViewGroup, int workItemId) {
        new handleDragDropTasks(colName, position, rootViewGroup, workItemId).execute();
    }

    private class handleDragDropTasks extends AsyncTask<Void, String, Void> {
        private ProgressDialog pd;
        private ViewGroup rootViewGroup;
        private int position;
        private String colName;
        private String query;
        private int workItemId;
        private String patchUrl;
        private String WorkIemDetails;

        public handleDragDropTasks(String colName, int position, ViewGroup rootViewGroup, int workItemId) {
            this.colName = colName;
            this.position = position;
            this.rootViewGroup = rootViewGroup;
            this.workItemId = workItemId;
            this.patchUrl = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/wit/workitems/" + this.workItemId + "?api-version=1.0";
            this.query = "[{\"op\": \"add\",\"path\": \"/fields/" + Constants.BOARD_OB.getColFieldName() + "\",\"value\": \"" + colName + "\"}]";
        }

        @Override
        protected void onPreExecute() {
            //pd = new ProgressDialog(Constants.CONTEXT);
            //pd.setMessage("Saving...");
            //pd.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                WorkIemDetails = oAuth2Helper.executeApiPatchCall(patchUrl, query);
                Log.i("patch", "workitem details updated=" + WorkIemDetails);

                publishProgress(WorkIemDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i(Constants.PROGRESS_TAG, "progress recvd for workitem:" + values[0]);
            Log.i(Constants.PROGRESS_TAG, "calling to add from patch");
            Constants.BOARD_OB.populateCol(values[0], colName, position, rootViewGroup);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null) {
                pd.dismiss();
            }
            Toast.makeText(Constants.CONTEXT, "card " + workItemId + "added to Column" + colName, Toast.LENGTH_SHORT).show();
        }
    }

    public void initiateAddCardTask(String colName,ViewGroup rootViewGroup,String title){
        new handleAddCardTask(colName,rootViewGroup,title).execute();
    }

    //TODO : Make calls here
    private class handleAddCardTask extends AsyncTask<Void, String, Void> {
        private String colName;
        private String title;
        private ViewGroup rootViewGroup;
        public handleAddCardTask(String colName,ViewGroup rootViewGroup,String title) {
            this.colName=colName;
            this.title=title;
            this.rootViewGroup=rootViewGroup;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String query =  "";

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

//TODO : Set prefs and oauth2helper
//TODO : Create methods for each action and donot se set uri method
//TODO : Return From do in Background

