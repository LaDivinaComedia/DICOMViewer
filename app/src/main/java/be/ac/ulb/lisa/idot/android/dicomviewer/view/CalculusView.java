package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.graphics.PointF;


/**
 *
 */
public class CalculusView {
    /**
     * Method calculate the distance in real measure considering the scale of view
     * @param mPoints - array with points on display
     * @param mPixelSpacingX - information about pixel spacing on imgae from DICOM file, first part of DICOM tag 0028:0030
     * @param mPixelSpacingY - information about pixel spacing on image from DICOM file, second part of DICOM tag 0028:0030
     * @param mScaleFactor - current scale factor of image on which try to calculate the distance.
     * @return  - distance in mm.
     */
    public static float getRealDistance(PointF[] mPoints, float mPixelSpacingX, float mPixelSpacingY, double mScaleFactor){
        float distanceX = (float) (Math.abs(mPoints[0].x - mPoints[1].x) * mPixelSpacingX);
        float distanceY = (float) (Math.abs(mPoints[0].y - mPoints[1].y) * mPixelSpacingY);
        float distance = (float)(Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)) / mScaleFactor);
        return distance;
    }

    public static float getRealSquare(){
        return 0.0f;
    }

    public static float getRealAngle(){
        return 0.0f;
    }


}
