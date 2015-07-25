package com.example.android.animationsdemo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by t-anmo on 7/25/2015.
 */
public class OpenCardInEditMode implements View.OnClickListener {
    private ViewGroup rootViewGroup;
    private JSONObject cardFields=null;
    private String title;
    private String state;
    private String assignedTo;
    private String reason="";
    public OpenCardInEditMode(ViewGroup rootViewGroup,JSONObject cardFields) {
        this.rootViewGroup = rootViewGroup;
        this.cardFields = cardFields;
    }

    @Override
    public void onClick(View view) {
        try {
            this.title  = cardFields.getString("System.Title");
            this.assignedTo=cardFields.has("System.AssignedTo")?cardFields.getString("System.AssignedTo"):"Unassigned";
            this.reason=cardFields.has("System.Reason")?cardFields.getString("System.Reason"):"";
            this.state = cardFields.getString("System.State");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LayoutInflater layoutInflater
                = (LayoutInflater)Constants.CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.card_view_edit_mode, null);
        EditText titleEditText = (EditText) popupView.findViewById(R.id.titleEditText);
        TextView idTextView = (TextView) popupView.findViewById(R.id.idTextView);
        Button btnClose = (Button) popupView.findViewById(R.id.btnClose);
        Button btnSave = (Button) popupView.findViewById(R.id.btnSave);
        ImageView close = (ImageView) popupView.findViewById(R.id.close);
        EditText reasonEditView = (EditText) popupView.findViewById(R.id.reasonEditText);

        titleEditText.setText(title);
        idTextView.setText(String.valueOf(view.getId()));
        reasonEditView.setText(reason);

        final PopupWindow popupWindow = new PopupWindow( popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);

        //Toast.makeText(Constants.CONTEXT,"Clicked this button",Toast.LENGTH_SHORT).show();
        popupWindow.showAtLocation(rootViewGroup, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);

        popupWindow.setFocusable(true);
        popupWindow.update();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Make rest Calls.
            }
        });
    }
}
