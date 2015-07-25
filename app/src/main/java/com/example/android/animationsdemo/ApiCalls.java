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

    private SharedPreferences prefs; //= PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
    private OAuth2Helper oAuth2Helper; //= new OAuth2Helper(prefs);



    public void initiateHandleDragDropTasks(String colName, int position, ViewGroup rootViewGroup, int workItemId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
        oAuth2Helper = new OAuth2Helper(prefs);
        new handleDragDropTasks(colName,position,rootViewGroup,workItemId).execute();
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
            this.patchUrl = Constants.ACCOUNT_URL+"/DefaultCollection/_apis/wit/workitems/" + this.workItemId + "?api-version=1.0";
            this.query = "[{\"op\": \"add\",\"path\": \"/fields/" + Constants.BOARD_OB.getColFieldName() + "\",\"value\": \"" + colName + "\"}]";
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(Constants.CONTEXT);
            pd.setMessage("Saving...");
            pd.setCancelable(false);
            pd.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                WorkIemDetails = oAuth2Helper.executeApiPatchCall(patchUrl,query);
                Log.i("patch","workitem details updated="+WorkIemDetails);

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
            Log.i(Constants.PROGRESS_TAG,"calling to add from patch");
            Constants.BOARD_OB.populateCol(values[0], colName, position, rootViewGroup);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null) {
                pd.dismiss();
            }
            Toast.makeText(Constants.CONTEXT, "card "+workItemId+"added to Column"+colName, Toast.LENGTH_SHORT).show();
        }
    }


   /* private class HandleDragDropTasksThread extends Thread {
        private String columnsJSON = null;
        private String colFieldName;
        private String getColFieldNameJSON;
        private String columnsJSONQuery = "https://anmostart.visualstudio.com/DefaultCollection/trialProject/TrialProject Team/_apis/work/boards/backlog items/columns?api-version=2.0-preview";
        private String getColFieldNameQuery="https://anmostart.visualstudio.com/DefaultCollection/_apis/wit/workitems/1?api-version=1.0";
        OAuth2Helper oAuth2Helper;

        //TODO : Make setters and getters like get board get team to form columnsJSONQuery
        public CreateColumnsThread(OAuth2Helper oAuth2Helper) {
            this.oAuth2Helper = oAuth2Helper;
        }

        public CreateColumnsThread() {
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            try {

                columnsJSON = oAuth2Helper.executeApiGetCall(columnsJSONQuery);
                getColFieldNameJSON = oAuth2Helper.executeApiGetCall(getColFieldNameQuery);
                Log.i(Constants.TAG, "Received response from API : " + columnsJSON);
                JSONObject rootObj = new JSONObject(columnsJSON);
                setColNo(rootObj.getInt("count"));
                Log.i(Constants.TAG, "Col No " + rootObj.getInt("count"));
                JSONArray columns = rootObj.getJSONArray("value");
                for (int i = 0; i < getColNo(); i++) {
                    JSONObject column;
                    column = columns.getJSONObject(i);
                    setColName(i, column.getString("name"));
                }
                JSONObject rootObj1 = new JSONObject(getColFieldNameJSON);
                JSONObject fields = rootObj1.getJSONObject("fields");
                for(int i=0;i<fields.length();i++){
                    String fieldName = fields.names().getString(i);
                    Log.i("value to check", "the substring to check is for i=:" + i + "and \n the string is " + fieldName.substring(Math.max(0, fieldName.length() - "_Kanban.Column".length())));
                    if(Objects.equals(fieldName.substring(Math.max(0, fieldName.length() - "_Kanban.Column".length())), "_Kanban.Column")){
                        Log.i("field name","field name is inside the thread :"+fieldName);
                        setColFieldName(fieldName);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                columnsJSON = ex.getMessage();
            }
        }
    }*/

}

//TODO : Set prefs and oauth2helper
//TODO : Create methods for each action and donot se set uri method
//TODO : Return From do in Background
