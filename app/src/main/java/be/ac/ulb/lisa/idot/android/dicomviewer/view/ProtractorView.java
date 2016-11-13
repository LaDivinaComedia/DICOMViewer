package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Iggytoto on 08.10.2016.
 */
public class ProtractorView extends ToolView implements View.OnTouchListener {
    private float mAngle = 0; // Angle label
    
    public ProtractorView(Context context) {
        super(context);
    }

    public ProtractorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProtractorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reset() {
        mAngle = 0;
        mCurrent = null;
        mPoints = new PointF[3];
    }

    public float getAngle() {
        return mAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPoints[0] != null) {
            canvas.drawCircle(mPoints[0].x, mPoints[0].y, mRadius, mPaint);
            if (mPoints[1] != null) {
                canvas.drawCircle(mPoints[1].x, mPoints[1].y, mRadius, mPaint);
                canvas.drawLine(mPoints[0].x, mPoints[0].y, mPoints[1].x, mPoints[1].y, mPaint);
                if (mPoints[2] != null) {
                    canvas.drawCircle(mPoints[2].x, mPoints[2].y, mRadius, mPaint);
                    canvas.drawLine(mPoints[1].x, mPoints[1].y, mPoints[2].x, mPoints[2].y, mPaint);
                    float margin = 50;
                    float height = canvas.getHeight() - mPaint.getTextSize() - margin;
                    canvas.drawText(String.format("%.2f deg.", mAngle), margin, height, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (checkBounds(point)) {
                    if (mPoints[0] == null || pointIsSelected(event, mPoints[0])) {
                        mCurrent = mPoints[0] = point;
                        updateAngle();
                    } else if (mPoints[1] == null || pointIsSelected(event, mPoints[1])) {
                        mCurrent = mPoints[1] = point;
                        updateAngle();
                    } else if (mPoints[2] == null || pointIsSelected(event, mPoints[2])) {
                        mCurrent = mPoints[2] = point;
                        updateAngle();
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
                        updateAngle();
                        this.invalidate();
                    }
                }else if (mTouchPoint == null) {
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

    private void updateAngle() {
        if (mPoints[0] != null && mPoints[1] != null && mPoints[2] != null)
            mAngle = Calculus.getRealAngle(new PointF[]{mPoints[0], mPoints[1], mPoints[2]},
                    mPixelSpacing[0], mPixelSpacing[1]);
    }

}
