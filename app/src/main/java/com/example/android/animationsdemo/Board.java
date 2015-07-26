package com.example.android.animationsdemo;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by t-anmo on 7/14/2015.
 */
public class Board extends MainActivity {
    private int colNo;
    private String[] colName;
    private Thread t;

    public String getColFieldName() {
        return colFieldName;
    }

    public void setColFieldName(String colFieldName) {
        this.colFieldName = colFieldName;
    }

    private String colFieldName;


    public ArrayList<String> getMembers() {
        return members;
    }

    private ArrayList<String> members;

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
        colName = new String[colNo];
    }

    public String getColName(int i) {
        return colName[i];
    }

    public void setColName(int i, String colName) {
        this.colName[i] = colName;
    }

    public void startCreateColumnsThread(OAuth2Helper oAuth2Helper) {
        t = new CreateColumnsThread(oAuth2Helper);
        t.start();

    }

    public void stopCreateColumnsThread() {
        try {
            Log.i("Thread joined", "Thread t has been joined");
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public class CreateColumnsThread extends Thread {
        private String columnsJSON = null;
        private String membersArrayJSON;
        private String getColFieldNameJSON;
        private String columnsJSONQuery = Constants.ACCOUNT_URL + "/DefaultCollection/" + Constants.PROJECT + "/" + Constants.TEAM + "/_apis/work/boards/" + Constants.BOARD_NAME + "/columns?api-version=2.0-preview";
        private String getColFieldNameQuery = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/wit/workitems/1?api-version=1.0";
        private String getMemberNamesQuery = Constants.ACCOUNT_URL + "/DefaultCollection/_apis/projects/" + Constants.PROJECT + "/teams/" + Constants.TEAM + "/members/?api-version=1.0";
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
                membersArrayJSON = oAuth2Helper.executeApiGetCall(getMemberNamesQuery);
                Log.i(Constants.TAG, "Received response from API : " + columnsJSON);
                JSONObject rootObj = new JSONObject(columnsJSON);
                setColNo(rootObj.getInt("count"));
                Log.i(Constants.TAG, "Col No " + rootObj.getInt("count"));
                JSONArray columns = rootObj.getJSONArray("value");
                //Get Col Names
                for (int i = 0; i < getColNo(); i++) {
                    JSONObject column;
                    column = columns.getJSONObject(i);
                    setColName(i, column.getString("name"));
                }
                ////to get and store the field name for columns as it varies from account to account
                JSONObject rootObj1 = new JSONObject(getColFieldNameJSON);
                JSONObject fields = rootObj1.getJSONObject("fields");
                for (int i = 0; i < fields.length(); i++) {
                    String fieldName = fields.names().getString(i);
                    Log.i("value to check", "the substring to check is for i=:" + i + "and \n the string is " + fieldName.substring(Math.max(0, fieldName.length() - "_Kanban.Column".length())));
                    if (Objects.equals(fieldName.substring(Math.max(0, fieldName.length() - "_Kanban.Column".length())), "_Kanban.Column")) {
                        Log.i("field name", "field name is inside the thread :" + fieldName);
                        setColFieldName(fieldName);
                        break;
                    }
                }

                ///set members array to be used later in Assigned To
                JSONObject rootObj3 = new JSONObject(membersArrayJSON);
                JSONArray nameJSONArray = rootObj3.getJSONArray("value");
                int count = rootObj3.getInt("count");
                members = new ArrayList<>();
                members.add("");
                for (int i = 0; i < count; i++) {
                    String memberName = nameJSONArray.getJSONObject(i).getString("displayName");
                    members.add(memberName);
                }
                //if no one assigned the task


            } catch (Exception ex) {
                ex.printStackTrace();
                columnsJSON = ex.getMessage();
            }
        }
    }

    public void populateCol(String workItemDetails, String colName, int position, ViewGroup rootViewGroup) {
        Log.i("rootView", "rootViewGroup=" + rootViewGroup);
        Log.i(Constants.TAG, "+++++Came to populate col with col name: " + colName + "\nand details=\n" + workItemDetails);
        try {
            JSONObject rootObj = new JSONObject(workItemDetails);
            int id = rootObj.getInt("id");
            Log.i(Constants.TAG, "Found Id" + id);
            JSONObject fields = rootObj.getJSONObject("fields");
            String title = fields.getString("System.Title");
            Log.i(Constants.TAG, "Found title" + title);
            final String state = fields.getString("System.State");
            Log.i(Constants.TAG, "Found state" + id);
            LayoutInflater inflater = LayoutInflater.from(Constants.CONTEXT);
            @SuppressLint("InflateParams") View cardBlock = inflater.inflate(R.layout.card_layout, null, false);
            Log.i(Constants.TAG, "Layout inflated");
            final TextView txt = (TextView) cardBlock.findViewById(R.id.card_title);
            final ImageView expandImageButton = (ImageView) cardBlock.findViewById(R.id.expandImage);
            ViewGroup parentColumnView = (ViewGroup) rootViewGroup.findViewWithTag("col" + colName);
            Log.i(Constants.TAG, "Viewgroup found" + parentColumnView.getTag());
            expandImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TextView assignedToView;
                    TextView stateView;
                    ViewGroup parent = (ViewGroup) view.getParent();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout ll = new LinearLayout(Constants.CONTEXT);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    String tag = "expandedView";
                    ll.setTag(tag);
                    stateView = new TextView(Constants.CONTEXT);
                    stateView.setText(state);
                    stateView.setTag(tag);
                    params.addRule(RelativeLayout.BELOW, txt.getId());
                    //ll.addView(assignedToView);
                    ll.addView(stateView);
                    //dynamicLayoutsTags.add(tag);
                    ViewGroup checkGroup = (ViewGroup) parent.findViewWithTag("expandedView");
                    if (checkGroup == null) {

                        parent.addView(ll, params);
                        expandImageButton.setImageResource(android.R.drawable.arrow_up_float);
                    } else {


                        parent.removeView(parent.findViewWithTag("expandedView"));
                        expandImageButton.setImageResource(android.R.drawable.arrow_down_float);
                    }
                }
            });
            cardBlock.setId(id);
            cardBlock.setTag("Draggable");
            txt.setText(title);
            Log.i(Constants.TAG, "Title set for witem : " + id);
            cardBlock.setOnLongClickListener(new MyClickListener());
            cardBlock.setOnDragListener(new MyDragListener(rootViewGroup));
            cardBlock.setOnClickListener(new OpenCardInEditMode(rootViewGroup, fields));

            parentColumnView.addView(cardBlock, position);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void refreshPage(){
        Constants.PAGER.setOffscreenPageLimit(0);
        Constants.PAGER.getAdapter().notifyDataSetChanged();
        Constants.PAGER.setOffscreenPageLimit(Constants.NUM_PAGES);
    }


}
