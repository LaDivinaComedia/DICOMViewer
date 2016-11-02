package be.ac.ulb.lisa.idot.dicom.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladyslav Vasyliev
 */
public class DICOMAnnotation {
    private String mLayerName;
    private int mLayerOrder;
    private List<DICOMTextObject> mTextObjects;
    private List<DICOMGraphicObject> mGraphicObjects;

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
