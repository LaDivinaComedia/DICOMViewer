package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

/**
 * @author Vladyslav Vasyliev
 *         Created on 09.10.2016
 */
public abstract class ToolView extends ImageView {
    protected float mThresholdDistance = 70;
    protected float mRadius;            // Radius of the end points
    protected float mDistance;          // Label with the value of the ruler
    protected float[] mPixelSpacing;    // Pixel spacing of the image. 0 - x axis, 1 - y axis
    protected Paint mPaint;             // Paint that is used to draw the ruler line
    protected PointF mTouchPoint; // Current touch point
    protected PointF mCurrent;    // Currently selected end point of the ruler
    protected PointF[] mPoints;         // Key points of the tool

    public ToolView(Context context) {
        super(context);
        init();
    }

    public ToolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mRadius = 10;
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(getResources().getDimension(R.dimen.text_size));
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPoints = null;
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

    public float[] getPixelSpacing() {
        return mPixelSpacing;
    }

    public void setPixelSpacing(float[] mPixelSpacing) {
        this.mPixelSpacing = mPixelSpacing;
    }

    protected boolean checkBounds(PointF point) {
        if (point == null)
            return false;
        int width = this.getWidth();
        int height = this.getHeight();
        return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height;
    }

    protected boolean pointIsSelected(MotionEvent event, PointF point) {
        if (point == null)
            return false;
        float x = event.getX(), y = event.getY();
        float dist = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2.0));
        return dist <= mThresholdDistance;
    }

    protected void translatePoints(PointF[] points, float dx, float dy) {
        if (points != null && points.length < 1)
            return;
        float minX, minY, maxX, maxY;
        minX = maxX = points[0].x;
        minY = maxY = points[0].y;
        for (PointF point : points) {
            if (point.x < minX)
                minX = point.x;
            else if (point.x > maxX)
                maxX = point.x;
            if (point.y < minY)
                minY = point.y;
            else if (point.y > maxY)
                maxY = point.y;
        }
        int width = this.getWidth();
        int height = this.getHeight();
        if (minX + dx < 0)
            dx = -minX;
        if (maxX + dx >= width)
            dx = width - maxX;
        if (minY + dy < 0)
            dy = minY;
        if (maxY + dy >= height)
            dy = height - maxY;
        for (PointF point : points) {
            point.x += dx;
            point.y += dy;
        }
    }

}
