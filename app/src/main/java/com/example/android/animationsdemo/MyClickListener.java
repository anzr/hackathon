package com.example.android.animationsdemo;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by t-anmo on 7/16/2015.
 */

    public final class MyClickListener implements View.OnTouchListener{
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

        view.startDrag(data, //data to be dragged
                shadowBuilder, //drag shadow
                view, //local data about the drag and drop operation
                0   //no needed flags
        );
        view.setVisibility(View.INVISIBLE);

        return false;
    }
    }
//TODO  : may be added in place of column inflaters depending on the performance
 /*LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / _noOfColumns));
            ll.setTag("ll" + _columns[i]);

            parent.addView(ll);
            TextView tv = new TextView(context);
            tv.setText(_columns[i]);
            tv.setAllCaps(true);
            ll.addView(tv);
            Button addCard = new Button(context);
            addCard.setText("+New Card");
            addCard.setTag(_columns[i]);
            //addCard.setWidth();
            ll.addView(addCard);


            ScrollView sv = new ScrollView(context);
            sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll.addView(sv);

            LinearLayout cardContainerLayout = new LinearLayout(context);
            cardContainerLayout.setOrientation(LinearLayout.VERTICAL);
            cardContainerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            cardContainerLayout.setTag("col" + _columns[i]);
            cardContainerLayout.setId(position);
            sv.addView(cardContainerLayout);
            cardContainerLayout.setMinimumHeight(height);
            cardContainerLayout.setMinimumWidth(width / MAX_COL_DISPLAYED);
            cardContainerLayout.setOnDragListener(new MyDragListener());
            */