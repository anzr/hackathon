package com.example.android.animationsdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by t-anmo on 7/25/2015.
 */
public class OpenCardInEditMode implements View.OnClickListener {
    private ViewGroup rootViewGroup;
    private JSONObject cardFields = null;
    private String title;
    private String state;
    private String assignedTo;
    private String reason = "";
    //new variables to build query if different from old ones
    private String newTitle;
    private String newState;
    private String newReason;
    private String newAssignedTo;
    private int workItemId;
    private int position;
    private ViewGroup parent;

    public OpenCardInEditMode(ViewGroup rootViewGroup, JSONObject cardFields) {
        this.rootViewGroup = rootViewGroup;
        this.cardFields = cardFields;
    }

    @Override
    public void onClick(View view) {
        try {
            parent = (ViewGroup) view.getParent();
            this.position = parent.indexOfChild(view);
            Toast.makeText(Constants.CONTEXT,"The position is:"+ this.position,Toast.LENGTH_LONG).show();
            this.title = cardFields.getString("System.Title");
            this.assignedTo = cardFields.has("System.AssignedTo") ? cardFields.getString("System.AssignedTo").split("<", 2)[0].trim() : "";
            this.reason = cardFields.has("System.Reason") ? cardFields.getString("System.Reason") : "";
            this.state = cardFields.getString("System.State");
            this.workItemId = view.getId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LayoutInflater layoutInflater
                = (LayoutInflater) Constants.CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.card_view_edit_mode, null);
        final EditText titleEditText = (EditText) popupView.findViewById(R.id.titleEditText);
        TextView idTextView = (TextView) popupView.findViewById(R.id.idTextView);
        Button btnClose = (Button) popupView.findViewById(R.id.btnClose);
        Button btnSave = (Button) popupView.findViewById(R.id.btnSave);
        ImageView close = (ImageView) popupView.findViewById(R.id.close);
        final EditText reasonEditView = (EditText) popupView.findViewById(R.id.reasonEditText);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Constants.CONTEXT, android.R.layout.simple_spinner_item, Constants.BOARD_OB.getMembers());
        final Spinner spinAssignedTo = (Spinner) popupView.findViewById(R.id.spinnerAsignedTo);
        spinAssignedTo.setAdapter(adapter);
        Log.i("Edit", "Assigned to got as:" + assignedTo);

        for (int i = 0; i < Constants.BOARD_OB.getMembers().size(); i++) {
            Log.i("Edit", "condition checked for i=" + i + " and the checked va;ue is:" + Constants.BOARD_OB.getMembers().get(i));
            if (assignedTo.equals(Constants.BOARD_OB.getMembers().get(i))) {
                Log.i("Edit", " assigned to found:" + Constants.BOARD_OB.getMembers().get(i));
                spinAssignedTo.setSelection(i);
            }
        }
        spinAssignedTo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinAssignedTo.getSelectedItemPosition();
                        //Toast.makeText(Constants.CONTEXT, "You have selected " + Constants.BOARD_OB.getMembers().get(position), Toast.LENGTH_LONG).show();
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                        // TODO Auto-generated method stub

                    }

                }
        );
        titleEditText.setText(title);
        idTextView.setText(String.valueOf(this.workItemId));
        reasonEditView.setText(reason);

        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);

        //Toast.makeText(Constants.CONTEXT,"Clicked this button",Toast.LENGTH_SHORT).show();
        popupWindow.showAtLocation(rootViewGroup, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setHeight(rootViewGroup.getHeight());
        popupWindow.setFocusable(true);
        popupWindow.update();
        btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupWindow.dismiss();
                                        }
                                    }

        );
        close.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         popupWindow.dismiss();
                                     }
                                 }

        );
        btnSave.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           newTitle = titleEditText.getText().toString();
                                           newReason = reasonEditView.getText().toString();
                                           newAssignedTo = Constants.BOARD_OB.getMembers().get(spinAssignedTo.getSelectedItemPosition());
                                           popupWindow.dismiss();
                                           new saveEditedOptions().execute();
                                       }
                                   }

        );
    }

    private class saveEditedOptions extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = new ProgressDialog(Constants.CONTEXT);
        private String updatedWorkItemDetails;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Constants.CONTEXT);
            pd.setMessage("Saving your changes...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //instead of refreshin we can add and remove views from the parent
            Constants.BOARD_OB.refreshPage();
            //parents tag is in the format "col<columnName>"
            String colName = parent.getTag().toString().substring(3);
            //TODO : Once patch call works update refresh page with adding card to columns simply
            //remove the current view and add this one
            // Constants.BOARD_OB.populateCol(updatedWorkItemDetails,colName,position,rootViewGroup);

            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String query = "[";
            if (!title.equals(newTitle.trim()))
                query += "{\"op\": \"add\",\"path\": \"/fields/System.Title\",\"value\": \"" + newTitle + "\"},";
            if (!assignedTo.equals(newAssignedTo))
                query += "{\"op\": \"add\",\"path\": \"/fields/System.AssignedTo\",\"value\": \"" + newAssignedTo + "\"},";
            if (!reason.equals(newReason.trim()))
                query += "{\"op\": \"add\",\"path\": \"/fields/System.Reason\",\"value\": \"" + newReason + "\"},";
            // add state too and get column from mappings then patch
            if (query.endsWith(","))
                query = query.substring(0, query.length() - 1);
            query += "]";

            String patchUrl = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/wit/workitems/" + workItemId + "?api-version=1.0";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Constants.CONTEXT);
            OAuth2Helper oAuth2Helper = new OAuth2Helper(prefs);
            try {

                updatedWorkItemDetails = oAuth2Helper.executeApiPatchCall(patchUrl, query);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
