package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Vladyslav Vasyliev
 *         Created on 04.10.2016
 */
public class RulerView extends ToolView implements View.OnTouchListener {
    // Start and end points of the ruler line are contained in mPoints array
    private float mScaleFactor; // Scale factor of the current image

    public RulerView(Context context) {
        super(context);
        reset();
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        reset();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        reset();
    }

    public void reset() {
        mDistance = 0;
        mCurrent = null;
        mPoints = new PointF[2];
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public void setScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }

    private void calculateRealDistance() {
        if (mPoints[0] != null && mPoints[1] != null)
            mDistance = CalculusView.getRealDistance(mPoints,
                    mPixelSpacing[0], mPixelSpacing[1], mScaleFactor);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPoints[0] == null || pointIsSelected(event, mPoints[0])) {
                    if (checkBounds(point))
                        mCurrent = mPoints[0] = point;
                } else if (mPoints[1] == null || pointIsSelected(event, mPoints[1])) {
                    if (checkBounds(point)) {
                        mCurrent = mPoints[1] = point;
                        calculateRealDistance();
                    }
                }
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrent != null) {
                    if (checkBounds(point)) {
                        mCurrent.x = event.getX();
                        mCurrent.y = event.getY();
                        calculateRealDistance();
                        this.invalidate();
                    }
                } else if (mTouchPoint == null) {
                    mTouchPoint = point;
                } else {
                    translatePoints(mPoints, event.getX() - mTouchPoint.x,
                            event.getY() - mTouchPoint.y);
                    mTouchPoint = point;
                    this.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchPoint = mCurrent = null;
                this.invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPoints[0] != null) {
            canvas.drawCircle(mPoints[0].x, mPoints[0].y, mRadius, mPaint);
            if (mPoints[1] != null) {
                canvas.drawCircle(mPoints[1].x, mPoints[1].y, mRadius, mPaint);
                canvas.drawLine(mPoints[0].x, mPoints[0].y, mPoints[1].x, mPoints[1].y, mPaint);
                float margin = 50;
                float height = canvas.getHeight() - mPaint.getTextSize() - margin;
                canvas.drawText(String.format("%.2f mm", mDistance), margin, height, mPaint);
            }
        }
    }
}
