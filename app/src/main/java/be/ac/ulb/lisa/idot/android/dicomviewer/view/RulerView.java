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

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.Interfaces.PointsMovedListener;

/**
 * @author Vladyslav Vasyliev
 *         Created on 04.10.2016
 */
public class RulerView extends ImageView implements View.OnTouchListener {
    private float mThresholdDistance = 70;
    private float mRadius;      // Radius of the end points
    private float mDistance;    // Label with the value of the ruler
    private PointF mStart;      // Start point of the ruler line
    private PointF mEnd;        // End point of the ruler line
    private PointF mCurrent;    // Currently selected ending of the ruler line
    private Paint mPaint;       // Paint that is used to draw the ruler line
    private PointsMovedListener changedListener;

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
        mRadius = 10;
        mCurrent = mStart = mEnd = null;
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
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

    public float getThresholdDistance() {
        return mThresholdDistance;
    }

    public void setThresholdDistance(float mThresholdDistance) {
        this.mThresholdDistance = mThresholdDistance;
    }

    public void setRulerMovedListener(PointsMovedListener changedListener) {
        this.changedListener = changedListener;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }

    public void reset() {
        mDistance = 0;
        mStart = mEnd = mCurrent = null;
    }

    private boolean pointIsSelected(MotionEvent event, PointF point) {
        if (point == null)
            return false;
        float x = event.getX(), y = event.getY();
        float dist = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2.0));
        return dist <= mThresholdDistance;
    }

    private boolean checkBounds(PointF point) {
        int width = this.getWidth();
        int height = this.getHeight();
        return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                PointF point = new PointF(event.getX(), event.getY());
                if (mStart == null || pointIsSelected(event, mStart)) {
                    if (checkBounds(point))
                        mCurrent = mStart = point;
                }
                else if (mEnd == null || pointIsSelected(event, mEnd)) {
                    if (checkBounds(point)) {
                        mCurrent = mEnd = new PointF(event.getX(), event.getY());
                        if (changedListener != null){
                            PointF[] pts = new PointF[3];
                            pts[0] = mStart;
                            pts[1] = mEnd;
                            changedListener.onPointsMoved(pts);
                        }
                    }
                }
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrent != null) {
                    point = new PointF(event.getX(), event.getY());
                    if (checkBounds(point)) {
                        mCurrent.x = event.getX();
                        mCurrent.y = event.getY();
                        if (changedListener != null){
                            PointF[] pts = new PointF[3];
                            pts[0] = mStart;
                            pts[1] = mEnd;
                            changedListener.onPointsMoved(pts);
                        }
                        this.invalidate();
                    }
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
                float margin = 50;
                float height = canvas.getHeight() - mPaint.getTextSize() - margin;
                canvas.drawText(String.format("%.2f mm", mDistance), margin, height, mPaint);
            }
        }
    }
}
