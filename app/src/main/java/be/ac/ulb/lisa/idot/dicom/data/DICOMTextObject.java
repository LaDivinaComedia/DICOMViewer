package be.ac.ulb.lisa.idot.dicom.data;

import android.graphics.PointF;

import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;

/**
 * @author Vladyslav Vasyliev
 */
public class DICOMTextObject {
    // text part of the annotation
    private String mText;
    private PointF mTextAnchor;
    private boolean mAnchorVisibility;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public PointF getTextAnchor() {
        return mTextAnchor;
    }

    public void setTextAnchor(PointF textAnchor) {
        mTextAnchor = textAnchor;
    }

    public boolean isAnchorVisible() {
        return mAnchorVisibility;
    }

    public void setAnchorVisibility(boolean anchorVisibility) {
        mAnchorVisibility = anchorVisibility;
    }

}
