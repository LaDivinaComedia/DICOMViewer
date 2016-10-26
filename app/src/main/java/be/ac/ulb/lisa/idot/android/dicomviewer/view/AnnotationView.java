package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by vvanichkov on 24.10.2016.
 */

public class AnnotationView extends ToolView implements View.OnTouchListener {
//    private ArrayList<Paint> mPaints = new ArrayList<Paint>();
    private ArrayList<CustomPath> mCustomPaths = new ArrayList<CustomPath>();
    private ArrayList<Paint> mPaints;
    private CustomPath mCurrentPath;
    private float mScaleFactor;
    private int mImageWidth;
    private int mImageHeight;
    private boolean inbound = true;
    public AnnotationView(Context context) {
        super(context);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }
    @Override
    protected void init(){
        mCurrentPath = new CustomPath();
        //initPaint();
        if(mPaints==null)
            mPaints = new ArrayList<Paint>();
        mPaints.add(initPaint());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = this.getWidth()/2;
        float centerY = this.getHeight()/2;
        if((centerX-this.mImageWidth*this.mScaleFactor/2)>x || (centerX+this.mImageWidth*this.mScaleFactor/2)<x
                ||
                (centerY-this.mImageHeight*this.mScaleFactor/2)>y || (centerY+this.mImageHeight*this.mScaleFactor/2)<y
                ){
            if((centerX-this.mImageWidth*this.mScaleFactor/2)>x)
                x=centerX-this.mImageWidth*this.mScaleFactor/2;
            else if((centerX+this.mImageWidth*this.mScaleFactor/2)<x)
                x=(centerX+this.mImageWidth*this.mScaleFactor/2);
            if((centerY-this.mImageHeight*this.mScaleFactor/2)>y)
                y=(centerY-this.mImageHeight*this.mScaleFactor/2);
            else if((centerY+this.mImageHeight*this.mScaleFactor/2)<y)
                y=(centerY+this.mImageHeight*this.mScaleFactor/2);
        }
        switch (event.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!inbound){
                    inbound=true;
                    init();
                    touch_start(x,y);
                }
                touch_move(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                mCurrentPath.mPath.lineTo(mCurrentPath.mStart.x,mCurrentPath.mStart.y);
                this.invalidate();
                this.mCustomPaths.add(this.mCurrentPath);
                init();
                break;
        }
        return true;
    }

    private void touch_up() {
        mCurrentPath.mPath.lineTo(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y);
        mCurrentPath.mEnd = new PointF(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y);
    }


    private void touch_move(float x, float y) {
        mCurrentPath.mPath.quadTo(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y,x,y);
        mCurrentPath.addToPath(new PointF(x,y));
    }

    private void touch_start(float x, float y) {
        mCurrentPath.mPath.reset();
        mCurrentPath.mPath.moveTo(x,y);
        mCurrentPath.mStart = new PointF(x,y);
        mCurrentPath.addToPath(mCurrentPath.mStart);
    }
    private Paint initPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(100);
        return mPaint;
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPath(mCurrentPath.mPath,mPaint);
        if(this.mCustomPaths.size()>0){
            for(int i=0;i<this.mCustomPaths.size();i++){
                canvas.drawPath(this.mCustomPaths.get(i).mPath,mPaints.get(i));
            }
        }
    }

    public void reset(){
        //TODO loading data from tag of annotation if it exists for new image.
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
        mPaints.add(initPaint());
    }
    public void setBounds(int imageWidth, int imageHeight, float scaleFactor){
        this.mImageWidth = imageWidth;
        this.mImageHeight = imageHeight;
        this.mScaleFactor = scaleFactor;

    }
    private class CustomPath{
        public PointF mStart;
        public PointF mEnd;
        public PointF mCurrent;
        public Path mPath = new Path();
        private ArrayList<PointF> paths = new ArrayList<>();

        public void addToPath(PointF point) {
            mCurrent=point;
            this.paths.add(point);
        }
    }
}
