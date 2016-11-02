package be.ac.ulb.lisa.idot.dicom.data;

import android.graphics.PointF;

/**
 * This class represent one text object of the DICOM's graphical annotation.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMTextObject {
    // Unformatted text. May be multiline.
    private String mText;
    // Anchor point of the text. Contains location in DICOM image pixels.
    private PointF mTextAnchor;
    // Visibility of the anchor point. If true, then it should be visible on the screen,
    // otherwise it should not be displayed.
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
