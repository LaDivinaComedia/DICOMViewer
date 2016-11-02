package be.ac.ulb.lisa.idot.dicom.data;

import android.graphics.PointF;

import java.util.*;

/**
 * This class represent one graphical object of the DICOM's graphical annotation.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMGraphicObject {
    // Possible types of the graphical object
    public interface GraphicTypes {
        String POINT = "POINT";
        String POLYLINE = "POLYLINE";
        String INTERPOLATED = "INTERPOLATED";
        String CIRCLE = "CIRCLE";
        String ELLIPSE = "ELLIPSE";

        Set<String> TYPES = new HashSet<>(Arrays.asList(POINT, POLYLINE, INTERPOLATED, CIRCLE, ELLIPSE));
    }

    // Number of points associated with this annotation.
    private int mNumberOfGraphicPoints;
    // Points of the graphical object. Specified in term of DICOM pixels.
    private List<PointF> mPoints;
    // Type of the graphical object. Possible types specified in GraphicTypes interface.
    private String mGraphicType;
    // If this flag is set then the graphical object should be filled with a color,
    // otherwise it should not.
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
