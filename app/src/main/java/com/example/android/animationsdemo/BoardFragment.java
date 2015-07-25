/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//TODO : Column Name cant be accessed directly

package com.example.android.animationsdemo;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class BoardFragment extends Fragment {
    private int MAX_COL_DISPLAYED;
    private int start;
    private int position;
    private ViewPager pager;
    private Context context;
    private String query;
    private OAuth2Helper oAuth2Helper;

    private ViewGroup rootViewGroup;
    private Board board;

    public static BoardFragment create(int position, int max_col_displayed) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("start", position * max_col_displayed);
        args.putInt("max_col_displayed", max_col_displayed);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start = getArguments().getInt("start");
        MAX_COL_DISPLAYED = getArguments().getInt("max_col_displayed");
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        return inflater
                .inflate(R.layout.anzer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        rootViewGroup = (ViewGroup) getView();
        pager = Constants.PAGER;
        context = getActivity();
        Constants.CONTEXT = context;
        board = Constants.BOARD_OB;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        oAuth2Helper = new OAuth2Helper(prefs);

        createCol();
        new PopulateColumns().execute();
        Log.i("Main thread Tag", "Main Thread Completed:" + position);
    }

    //TODO : the returned value is a jsonArray . Can be changed to Strings if other params are not used in future
    public void createCol() {
        int colNo = board.getColNo();
        Log.i("colNo", "colNo=" + colNo);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i("conditions", "codition start=" + Math.max(0, start) + "\n check = " + Math.min(start + MAX_COL_DISPLAYED, board.getColNo()) + "in createCOl");
        for (int i = Math.max(0, start); i < Math.min(start + MAX_COL_DISPLAYED, board.getColNo()); i++) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View columnView = inflater.inflate(R.layout.columns_layout, null, false);
            LinearLayout ll = (LinearLayout) columnView.findViewById(R.id.ll);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / MAX_COL_DISPLAYED));
            TextView tv = (TextView) columnView.findViewById(R.id.tv);
            tv.setText(board.getColName(i));
            Button addCard = (Button) columnView.findViewById(R.id.addCard);
            LinearLayout cardContainerLayout = (LinearLayout) columnView.findViewById(R.id.cardContainer);
            cardContainerLayout.setTag("col" + board.getColName(i));
            cardContainerLayout.setId(position);
            cardContainerLayout.setMinimumHeight(height);
            cardContainerLayout.setMinimumWidth(width / MAX_COL_DISPLAYED);
            Log.i("Drag", "Calling drag constructor ");
            cardContainerLayout.setOnDragListener(new MyDragListener(rootViewGroup));
            Log.i("Drag", "Drag Constructor Called ");
            addCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BoardFragment.this.addItem(view);
                }
            });
            //cardContainerLayout.setLayoutTransition(new LayoutTransition());

            rootViewGroup.addView(columnView);
        }
    }

    public void addItem(View view) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View newCard = inflater.inflate(R.layout.new_card_layout, null, false);
        newCard.setTag("newCard");
        ViewGroup parentCol = (ViewGroup) view.getParent();
        ViewGroup svCol = (ViewGroup) parentCol.getChildAt(parentCol.getChildCount() - 1);
        ViewGroup containerCol = (ViewGroup) svCol.getChildAt(0);
        View check = containerCol.findViewWithTag("newCard");
        if (check == null)
            containerCol.addView(newCard, 0);
        else containerCol.removeView(check);
        ImageView save = (ImageView) newCard.findViewById(R.id.saveNewCard);
        final EditText title = (EditText) newCard.findViewById(R.id.newCardTitle);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (isEmpty(title))
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
                else {
                    query = "";
                    pager.getAdapter().notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
                }    //
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private class PopulateColumns extends AsyncTask<Uri, String, Void> {
        private ProgressDialog pd;
        String workItemCount = null;
        String postUrl = Constants.ACCOUNT_URL+"/DefaultCollection/_apis/wit/wiql?api-version=1.0";

        @Override
        protected void onPreExecute() {
            if (position == pager.getCurrentItem()) {
                pd = new ProgressDialog(context);
                pd.setMessage("preparing your board...");
                pd.setCancelable(false);
                pd.show();
            }

        }

        @Override
        protected Void doInBackground(Uri... params) {
            for (int i = Math.max(0, start); i < Math.min(start + MAX_COL_DISPLAYED, board.getColNo()); i++)
                try {
                    String query = "{'query': 'Select [System.id] From WorkItems Where ["+board.getColFieldName()+"]=\"" + board.getColName(i) + "\"'}";
                    Log.i(Constants.TAG, "field name is : " + board.getColFieldName());
                    workItemCount = oAuth2Helper.executeApiPostCall(postUrl, query);
                    Log.i(Constants.TAG, "+++++Found workitem counts json : " + workItemCount);
                    JSONObject rootObj = new JSONObject(workItemCount);
                    JSONArray workItems = rootObj.getJSONArray("workItems");
                    for (int workItem = 0; workItem < workItems.length(); workItem++) {
                        int id = workItems.getJSONObject(workItem).getInt("id");
                        String getUrlForEachWorkitem = Constants.ACCOUNT_URL+"/DefaultCollection/_apis/wit/workItems/" + id;
                        String WorkIemDetails = oAuth2Helper.executeApiGetCall(getUrlForEachWorkitem);
                        Log.i(Constants.TAG, "+++++Found workitem Details for workitem" + id + " = " + WorkIemDetails);
                        String[] details = new String[2];
                        details[0] = WorkIemDetails;
                        details[1] = board.getColName(i);
                        publishProgress(details);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    workItemCount = ex.getMessage();
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.i(Constants.PROGRESS_TAG, "progress recvd for workitem:" + values[0]);
            Log.i(Constants.PROGRESS_TAG, "progress recvd for column Name:" + values[1]);
            board.populateCol(values[0], values[1], 0, rootViewGroup);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd != null) {
                pd.dismiss();
            }
        }
    }
}
