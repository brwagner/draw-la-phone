package com.bwjfstudios.drawlaphone.activity.game.draw.drawview;

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

/**
 * View used to draw pictures in game
 */
public class DrawView extends View {

    private ArrayList<ColorPath> paths; // All of the elements drawn by the user
    private Paint paint; // Paint object used for drawing
    private int size; // Width of the current stroke for the element
    private int color; // Color of the current element

    // Default initialization constructor
    public DrawView(Context context) {
        super(context);
        init(context);
    }

    // Init the path and paint and set size and color to defaults
    private void init(Context context) {
        this.paths = new ArrayList<ColorPath>();
        this.paint = new Paint();
        this.size = 10;
        this.color = this.getResources().getColor(R.color.black);
    }

    // Get the last path in the list
    private ColorPath getLastColorPath() {
        return this.paths.get(this.paths.size() - 1);
    }

    // Removes the last drawn segment
    public void undoPath() {
        if (this.paths.size() > 0) {
            this.paths.remove(this.paths.size() - 1);
        }
        // Redraw the view
        this.invalidate();
    }

    @Override // Decide what to do when user is drawing
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Add a new ColorPath to the list and move the first node to where the user touched
            this.paths.add(new ColorPath(this.size, this.color, new Path()));
            Path curPath = this.getLastColorPath().getPath();
            curPath.moveTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Draw a line from the previous node of the path to where the user's finger is
            Path curPath = this.getLastColorPath().getPath();
            curPath.lineTo(event.getX(), event.getY());
        }

        // Redraw the view
        this.invalidate();

        return true;
    }

    @Override // Called whenever invalidated and on start
    protected void onDraw(Canvas canvas) {
        // Set background
        canvas.drawColor(Color.WHITE);

        // Give the paths some width
        this.paint.setStyle(Paint.Style.STROKE);

        // Go through all of the elements the user has drawn and draw them with their given color and size
        for (ColorPath cPath : this.paths) {
            this.paint.setColor(cPath.getColor());
            this.paint.setStrokeWidth(cPath.getSize());
            canvas.drawPath(cPath.getPath(), this.paint);
        }
        super.onDraw(canvas);
    }

    // Returns the drawing
    public Bitmap getImage() {
        Bitmap screenShot;
        this.setDrawingCacheEnabled(true);
        screenShot = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        return screenShot;
    }

/////////////////////////////////////////////////////////////////////////////
//                        Setters and Getters
/////////////////////////////////////////////////////////////////////////////

    public void setSize(int brushSize) {
        this.size = brushSize;
    }

    public void setColor(int colorValue) {
        this.color = colorValue;
    }
}

