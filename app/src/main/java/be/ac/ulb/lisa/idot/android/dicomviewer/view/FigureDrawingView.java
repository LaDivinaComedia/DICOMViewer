package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

/**
 * Created by vvanichkov on 08.10.2016.
 */

public class FigureDrawingView extends ToolView implements View.OnTouchListener {
    private static final float TOUCH_TOLERANCE = 1;
    private Canvas mCanvas;     // Canvas on which is drawing view
    private float mRadius;      // Radius of the line
    private PointF mStart;      // Start point of the ruler line
    private PointF mEnd;        // End point of the ruler line
    private PointF mCurrent;    // Currently selected ending of the ruler line
    private Paint mPaint;       // Paint that is used to draw the line
    private Path mPath;         // Path that is contained path from start point to current
    private ArrayList<PointF> path = new ArrayList<PointF>();
    private Bitmap mBitmap;     // Bitmap with filled area.
    private RectF bounds = new RectF();       // Bounds in which is placed drawed figure
    private float mScaleFactor;  // Scale Factor of parent view(from DICOM Fragment)
    private Float mSquare;       // Square of end figure
    private int mAlpha = 100;


    public FigureDrawingView(Context context) {
        super(context);
        init();
    }

    public FigureDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FigureDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                //this.invalidate();
                mPath.lineTo(mStart.x,mStart.y);
                this.invalidate();
                this.buildDrawingCache();
                this.mBitmap = this.getDrawingCache();
                getSquare();
                break;
        }
        return true;
    }

    private void touch_up() {
        mPath.lineTo(mCurrent.x,mCurrent.y);
        mEnd = new PointF(mCurrent.x,mCurrent.y);
        this.path.add(mEnd);
        mPath.computeBounds(bounds,true);
    }


    private void touch_move(float x, float y) {
        float dx = Math.abs(x-mCurrent.x);
        float dy = Math.abs(y-mCurrent.y);
        //if(dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE){
        mPath.quadTo(mCurrent.x,mCurrent.y,x,y);
            //mPath.addRoundRect(mStart.x,mStart.y,x,y,5,5, Path.Direction.CW);
        mCurrent = new PointF(x,y);
        this.path.add(mCurrent);
       // }
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x,y);
        mStart = new PointF(x,y);
        mCurrent = new PointF(x,y);
        this.path.add(mCurrent);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);
        if(mEnd!=null){
            float margin = 50;
            float height = canvas.getHeight() - mPaint.getTextSize() - margin;
            canvas.drawText(String.format("%.2f mm2", mSquare), margin, height, mPaint);
        }
    }
    private void getSquare(){
        int starty = (int) ((int)bounds.centerY()-bounds.height()/2);
        int endy = (int) ((int)bounds.centerY()+bounds.height()/2);
        int startx = (int) ((int)bounds.centerX()-bounds.width()/2);
        int endx = (int) ((int)bounds.centerX()+bounds.width()/2);
        float result = 0;
        int count2 =0;
        if(mBitmap!=null){
            for(int i=starty;i<endy && i<mBitmap.getHeight();i++){
                for(int j=startx;j<endx && j<mBitmap.getWidth();j++){
                    if(mBitmap.getPixel(j,i)!= mPaint.getColor()){
                        count2++;
                    }
                }
            }
        }
        mSquare = CalculusView.getRealSquare(count2, mScaleFactor,mPixelSpacing[0],mPixelSpacing[1]);
    }

    public void reset(){
        mPath = new Path();
        this.init();
    }
    @Override
    protected void init() {
        mRadius = 10;
        mCurrent = mStart = mEnd = null;
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(100);
    }
    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void setScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }

}
