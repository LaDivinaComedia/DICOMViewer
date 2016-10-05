package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.view.View;

/**
 * @author Vladyslav Vasyliev
 *         Created on 04.10.2016
 */
public class RulerView extends ImageView implements View.OnTouchListener {
    private float mThresholdDistance = 70;
    private float mRadius;      // Radius of the end points
    private PointF mStart;      // Start point of the ruler line
    private PointF mEnd;        // End point of the ruler line
    private PointF mCurrent;    // Currently selected ending of the ruler line
    private Paint mPaint;       // Paint that is used to draw the ruler line
    private boolean mEndPoint;   // Shows whether a new line is drawn

    public RulerView(Context context) {
        super(context);
        init();
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEndPoint = false;
        mRadius = 10;
        mCurrent = mStart = mEnd = null;
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public float getmThresholdDistance() {
        return mThresholdDistance;
    }

    public void setmThresholdDistance(float mThresholdDistance) {
        this.mThresholdDistance = mThresholdDistance;
    }

    private boolean pointIsSelected(MotionEvent event, PointF point) {
        if (point == null)
            return false;
        float x = event.getX(), y = event.getY();
        float dist = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2.0));
        return dist <= mThresholdDistance;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mStart == null || pointIsSelected(event, mStart))
                    mCurrent = mStart = new PointF(event.getX(), event.getY());
                else if (mEnd == null || pointIsSelected(event, mEnd))
                    mCurrent = mEnd = new PointF(event.getX(), event.getY());
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrent != null) {
                    mCurrent.x = event.getX();
                    mCurrent.y = event.getY();
                    this.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrent = null;
                this.invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStart != null) {
            canvas.drawCircle(mStart.x, mStart.y, mRadius, mPaint);
            if (mEnd != null) {
                canvas.drawCircle(mEnd.x, mEnd.y, mRadius, mPaint);
                canvas.drawLine(mStart.x, mStart.y, mEnd.x, mEnd.y, mPaint);
            }
        }
    }
}
