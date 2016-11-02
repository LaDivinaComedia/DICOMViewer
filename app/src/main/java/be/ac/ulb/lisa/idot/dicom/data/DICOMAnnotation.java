package be.ac.ulb.lisa.idot.dicom.data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Graphic Layer. It may contain 0..N text and
 * graphic objects.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMAnnotation {
    private String mLayerName;          // Name of the layer (annotation).
    private int mLayerOrder;            // Z-index of the layer (annotation).
    private List<DICOMTextObject> mTextObjects;         // List of the text objects.
    private List<DICOMGraphicObject> mGraphicObjects;   // List of the graphic objects.

    public DICOMAnnotation() {
        mLayerOrder = -1;
        mTextObjects = new ArrayList<>();
        mGraphicObjects = new ArrayList<>();
    }

    public DICOMAnnotation(String name) {
        this();
        mLayerName = name;
    }

    public String getLayerName() {
        return mLayerName;
    }

    public void setLayerName(String layerName) {
        mLayerName = layerName;
    }

    public int getLayerOrder() {
        return mLayerOrder;
    }

    public void setLayerOrder(int layerOrder) {
        mLayerOrder = layerOrder;
    }

    public List<DICOMTextObject> getTextObjects() {
        return mTextObjects;
    }

    public List<DICOMGraphicObject> getGraphicObjects() {
        return mGraphicObjects;
    }

}
