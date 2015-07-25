package com.example.android.animationsdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by t-anmo on 7/24/2015.
 */
public class MyDragListener extends MainActivity implements View.OnDragListener {
    int list_position=0;
    private ViewGroup rootViewGroup;
    private final ViewPager pager=Constants.PAGER;
    private final Context context = Constants.CONTEXT ;
    Drawable normalShape;
    Drawable targetShape;
    ApiCalls apiCalls;
    public MyDragListener(ViewGroup rootViewGroup) {
        this.rootViewGroup=rootViewGroup;
         normalShape = Constants.CONTEXT.getResources().getDrawable(R.drawable.normal_shape);
         targetShape = Constants.CONTEXT.getResources().getDrawable(R.drawable.target_shape);
        Log.i("Drag", "Came to Constructor: rootViewGroup="+rootViewGroup.toString());
        apiCalls = new ApiCalls();
    }


   // ViewPager pager = (ViewPager) this.findViewById(R.id.pager);

    private ViewGroup parent;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onDrag(final View v, final DragEvent event) {

        if (v instanceof LinearLayout)
            parent = (ViewGroup) v;
        else
            parent = (ViewGroup) v.getParent();
        // Handles each of the expected events
        switch (event.getAction()) {

            //signal for the start of a drag and drop operation.
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;

            //the drag point has entered the bounding box of the View
            case DragEvent.ACTION_DRAG_ENTERED:
                parent.setBackground(targetShape);    //change the shape of the view
                list_position = parent.indexOfChild(v);
                break;
            //the user has moved the drag shadow outside the bounding box of the View
            case DragEvent.ACTION_DRAG_EXITED:
                parent.setBackground(normalShape);    //chan
                list_position = parent.indexOfChild(v);

                int SCREEN_WIDTH = rootViewGroup.getWidth();
                int offset = parent.getWidth() / 2;
                if (Math.abs(event.getX()) > SCREEN_WIDTH - offset)
                    pager.setCurrentItem(pager.getCurrentItem() + 1);

                if (Math.abs(event.getX()) < offset)
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                break;

            //drag shadow has been released,the drag point is within the bounding box of the View
            case DragEvent.ACTION_DROP:

                View view = (View) event.getLocalState();
                ViewGroup viewgroup = (ViewGroup) view.getParent();
                viewgroup.removeView(view);
                LinearLayout containView = (LinearLayout) parent;

                apiCalls.initiateHandleDragDropTasks(containView.getTag().toString().substring(3),list_position,rootViewGroup,view.getId());


                //containView.addView(view, list_position);
                //view.setVisibility(View.VISIBLE);
                break;

            //the drag and drop operation has concluded.
            // TODO : change the state of the wit
            case DragEvent.ACTION_DRAG_ENDED://go back to normal shape
                parent.setBackground(normalShape);
                if (dropEventNotHandled(event)) {
                    View curview = (View) event.getLocalState();
                    curview.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
        return true;

    }

    // handles the event if it dropped at someplace else.
    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
   }

}