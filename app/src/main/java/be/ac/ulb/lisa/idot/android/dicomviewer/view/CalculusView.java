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

    /**
    Method to calculate real angle by three points on DICOM picture
     * @param mPoints - array with three points on display
     * @param mPixelSpacingX - information about pixel spacing on imgae from DICOM file, first part of DICOM tag 0028:0030
     * @param mPixelSpacingY - information about pixel spacing on image from DICOM file, second part of DICOM tag 0028:0030
     * @return  - angle in degrees.
     */
    public static float getRealAngle(PointF[] mPoints,float mPixelSpacingX,float mPixelSpacingY){
            double ax = (mPoints[0].x - mPoints[1].x) * mPixelSpacingX;
            double ay = (mPoints[0].y - mPoints[1].y) * mPixelSpacingY;
            double bx = (mPoints[2].x - mPoints[1].x)* mPixelSpacingX;
            double by = (mPoints[2].y - mPoints[1].y)* mPixelSpacingY;

            double cosab = ((ax*bx + ay*by)/ (Math.sqrt(Math.pow(ax,2) + Math.pow(ay,2)) *
                    Math.sqrt(Math.pow(bx,2) + Math.pow(by,2))));

            return (float) ( Math.acos(cosab) * 180 / Math.PI);
    }


}
