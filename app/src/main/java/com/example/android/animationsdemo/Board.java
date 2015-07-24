package com.example.android.animationsdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by t-anmo on 7/14/2015.
 */
public class Board extends MainActivity {
    private int colNo;
    private String[] colName;
    private Thread t;

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
        private String apiResponse = null;
        private String query = "https://anmostart.visualstudio.com/DefaultCollection/trialProject/TrialProject Team/_apis/work/boards/backlog items/columns?api-version=2.0-preview";
        OAuth2Helper oAuth2Helper;

        //TODO : Make setters and getters like get board get team to form query
        public CreateColumnsThread(OAuth2Helper oAuth2Helper) {
            this.oAuth2Helper = oAuth2Helper;
        }

        public CreateColumnsThread() {
        }

        @Override
        public void run() {
            try {

                apiResponse = oAuth2Helper.executeApiCall(query);
                Log.i(Constants.TAG, "Received response from API : " + apiResponse);
                JSONObject rootObj = new JSONObject(apiResponse);
                setColNo(rootObj.getInt("count"));
                Log.i(Constants.TAG, "Col No " + rootObj.getInt("count"));
                JSONArray columns = rootObj.getJSONArray("value");
                for (int i = 0; i < getColNo(); i++) {
                    JSONObject column;
                    column = columns.getJSONObject(i);
                    setColName(i, column.getString("name"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                apiResponse = ex.getMessage();
            }
        }
    }

    public void populateCol(String workItemDetails, String colName,int position,ViewGroup rootViewGroup) {
        Log.i("rootView","rootViewGroup="+rootViewGroup);
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
            ViewGroup v = (ViewGroup) rootViewGroup.findViewWithTag("col" + colName);
            Log.i(Constants.TAG, "Viewgroup found" + v.getTag());
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

            v.addView(cardBlock,position);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
