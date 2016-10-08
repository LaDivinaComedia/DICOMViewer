package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;

/**
 * Created by Iggytoto on 08.10.2016.
 */

public class ProtractorView extends ImageView implements View.OnTouchListener {


    private float mAngle = 0; // Angle label
    private PointF mPointA = null;
    private PointF mPointB = null;
    private PointF mPointC = null;
    private PointF mSelectedPoint = null;
    private float mThresholdDistance = 70;
    private float mRadius = 10;      // Radius of the end points
    private DICOMMetaInformation metaInformation;
    private Paint mPaint;

    public ProtractorView(Context context) {
        super(context);
        initPaint();
    }

    public ProtractorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ProtractorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void reset() {
        mAngle = 0;
        mPointA = null;
        mPointB = null;
        mPointC = null;
    }

    public void setMetaInformation(DICOMMetaInformation metaInformation) {
        this.metaInformation = metaInformation;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPointA != null) {
            canvas.drawCircle(mPointA.x, mPointA.y, mRadius, mPaint);
            if (mPointB != null) {
                canvas.drawCircle(mPointB.x, mPointB.y, mRadius, mPaint);
                canvas.drawLine(mPointA.x, mPointA.y, mPointB.x, mPointB.y, mPaint);
                if (mPointC != null) {
                    canvas.drawCircle(mPointC.x, mPointC.y, mRadius, mPaint);
                    canvas.drawLine(mPointB.x, mPointB.y, mPointC.x, mPointC.y, mPaint);
                    float margin = 50;
                    float height = canvas.getHeight() - mPaint.getTextSize() - margin;
                    canvas.drawText(String.format("%.2f deg.", mAngle), margin, height, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                PointF point = new PointF(event.getX(), event.getY());
                if (checkBounds(point)) {
                    if (mPointA == null || pointIsSelected(event, mPointA)) {
                        mSelectedPoint = mPointA = point;
                        updateAngle();
                    } else if (mPointB == null || pointIsSelected(event, mPointB)) {
                        mSelectedPoint = mPointB = point;
                        updateAngle();
                    } else if (mPointC == null || pointIsSelected(event, mPointC)) {
                        mSelectedPoint = mPointC = point;
                        updateAngle();
                    }
                }
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectedPoint != null) {
                    point = new PointF(event.getX(), event.getY());
                    if (checkBounds(point)) {
                        mSelectedPoint.x = event.getX();
                        mSelectedPoint.y = event.getY();
                        updateAngle();
                        this.invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mSelectedPoint = null;
                this.invalidate();
                break;
        }
        return true;
    }

    private void updateAngle() {
        if (mPointA != null && mPointB != null && mPointC != null)
            mAngle = CalculusView.getRealAngle(new PointF[]{mPointA, mPointB, mPointC},
                    (float) metaInformation.getPixelSpacing()[0], (float) metaInformation.getPixelSpacing()[1]);
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

}
