package be.ac.ulb.lisa.idot.dicom.data;

import android.graphics.PointF;

import java.util.*;

/**
 * Created by vlad on 02.11.2016.
 */
public class DICOMGraphicObject {
    public interface GraphicTypes {
        String POINT = "POINT";
        String POLYLINE = "POLYLINE";
        String INTERPOLATED = "INTERPOLATED";
        String CIRCLE = "CIRCLE";
        String ELLIPSE = "ELLIPSE";

        Set<String> TYPES = new HashSet<>(Arrays.asList(POINT, POLYLINE, INTERPOLATED, CIRCLE, ELLIPSE));
    }
    // graphic part of the annotation
    private int mNumberOfGraphicPoints;
    private List<PointF> mPoints;
    private String mGraphicType;
    private boolean mGraphicFilled;

    public int getNumberOfGraphicPoints() {
        return mNumberOfGraphicPoints;
    }

    public void setNumberOfGraphicPoints(int numberOfGraphicPoints) {
        mNumberOfGraphicPoints = numberOfGraphicPoints;
    }

    public List<PointF> getPoints() {
        return mPoints;
    }

    public void setPoints(float[] points) {
        if (mPoints == null)
            mPoints = new ArrayList<>();
        else
            mPoints.clear();
        for (int i = 0; i < mNumberOfGraphicPoints * 2; i += 2)
            mPoints.add(new PointF(points[i], points[i + 1]));
    }

    public void setPoints(List<PointF> points) {
        mPoints = points;
    }

    public String getGraphicType() {
        return mGraphicType;
    }

    public void setGraphicType(String type) {
        mGraphicType = type;
    }

    public boolean isGraphicFilled() {
        return mGraphicFilled;
    }

    public void setGraphicFilled(boolean graphicFilled) {
        mGraphicFilled = graphicFilled;
    }

}
