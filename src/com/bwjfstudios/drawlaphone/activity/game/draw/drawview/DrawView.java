package com.bwjfstudios.drawlaphone.activity.game.draw.drawview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bwjfstudios.drawlaphone.R;

import java.util.ArrayList;

public class DrawView extends View {

    // all of the elements drawn by the user
    private ArrayList<ColorPath> paths;
    // paint object used for drawing
    private Paint paint;
    // width of the current stroke for the element
    private int size;
    // color of the current element
    private int color;

    // default initialization constructors
    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    //Setters and getters
    public void setSize(int brushSize) {
        this.size = brushSize;
    }

    public void setColor(int colorValue) {
        this.color = colorValue;
    }

    // init the path and paint and set size and color to defaults
    private void init(Context context) {
        this.paths = new ArrayList<ColorPath>();
        this.paint = new Paint();
        this.size = 10;
        this.color = this.getResources().getColor(R.color.black);
    }

    // get the last path in the list
    ColorPath getLastColorPath() {
        return this.paths.get(this.paths.size() - 1);
    }

    // removes the last drawn segment
    public void undoPath() {
        if (this.paths.size() > 0) {
            this.paths.remove(this.paths.size() - 1);
        }
        // redraw the view
        this.invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override // decide what to do when user is drawing
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // add a new ColorPath to the list and move the first node to where the user touched
            this.paths.add(new ColorPath(this.size, this.color, new Path()));
            Path curPath = this.getLastColorPath().getPath();
            curPath.moveTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // draw a line from the previous node of the path to where the user's finger is
            Path curPath = this.getLastColorPath().getPath();
            curPath.lineTo(event.getX(), event.getY());
        }

        // redraw the view
        this.invalidate();

        return true;
    }

    @Override // called whenever invalidated and on start
    protected void onDraw(Canvas canvas) {
        // set background
        canvas.drawColor(Color.WHITE);

        // give the paths some width
        this.paint.setStyle(Paint.Style.STROKE);

        // go through all of the elements the user has drawn and draw them with their given color and size
        for (ColorPath cPath : this.paths) {
            this.paint.setColor(cPath.getColor());
            this.paint.setStrokeWidth(cPath.getSize());
            canvas.drawPath(cPath.getPath(), this.paint);
        }
        super.onDraw(canvas);
    }

    // returns the drawing
    public Bitmap getImage() {
        Bitmap screenshot;
        this.setDrawingCacheEnabled(true);
        screenshot = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        return screenshot;
    }
}

