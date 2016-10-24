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
    private ArrayList<Paint> mPaints = new ArrayList<Paint>();
    private ArrayList<CustomPath> mCustomPaths = new ArrayList<CustomPath>();
    private CustomPath mCurrentPath;

    public AnnotationView(Context context) {
        super(context);
        init();
    }

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @Override
    protected void init(){
        mCurrentPath = new CustomPath();
        initPaint();
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
                mCurrentPath.mPath.lineTo(mCurrentPath.mStart.x,mCurrentPath.mStart.y);
                this.invalidate();
                this.mCustomPaths.add(this.mCurrentPath);
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
        mPaints.add(initPaint());
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
    }

    public void reset(){
        this.initPaint();
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
