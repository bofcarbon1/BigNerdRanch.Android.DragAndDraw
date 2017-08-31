package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by bofcarbon1 on 8/23/2017.
 */

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";

    //Track drawing state
    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();

    //Drawing
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    //Ch 29 Challenge 2 Rotation pointer Ids
    private int ptrID1, ptrID2;
    private MotionEvent mLastMultitouchEvent = null;

    //Used when creating the view in code
    public BoxDrawingView (Context context) {
        this(context, null);
    }

    //Used when infalting the view from XML
    public BoxDrawingView (Context context, AttributeSet attrs) {
        super(context, attrs);

        //Paint the boxes a nice trasnparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        //Paint the background off white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x );
            float right = Math.max(box.getOrigin().x, box.getCurrent().x );
            float top = Math.min(box.getOrigin().y, box.getCurrent().y );
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y );

            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    //Listen for and act on a touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                //Reset drawing state
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if(mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + "at x=" + current.x + ", y=" + current.y);

        return true;

    }

    //Ch29 Challenge - Save state for orientation change
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(TAG, "onSaveInstanceState started");
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        Gson gson = new Gson();
        String jsonList = gson.toJson(mBoxen);
        bundle.putString("test", jsonList);
        return bundle;
    }

    //Ch29 Challenge - Save state for orientation change
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i(TAG, "onRestoreInstanceState started");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String boxen = bundle.getString("test");
            Gson gson = new Gson();
            mBoxen = gson.fromJson(boxen, new TypeToken<List<Box>>(){}.getType());
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        super.onRestoreInstanceState(state);
    }


}
