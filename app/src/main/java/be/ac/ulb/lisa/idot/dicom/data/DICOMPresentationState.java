package be.ac.ulb.lisa.idot.dicom.data;

import java.util.List;

/**
 * @author Vladyslav Vasyliev
 */
public class DICOMPresentationState {
    private DICOMMetaInformation mMetaInformation;
    private List<DICOMAnnotation> mAnnotations;

    public DICOMPresentationState() {}

    public DICOMPresentationState(DICOMMetaInformation metaInformation,
                                  List<DICOMAnnotation> annotations) {
        mMetaInformation = metaInformation;
        mAnnotations = annotations;
    }

    public DICOMMetaInformation getMetaInformation() {
        return mMetaInformation;
    }

    public void setMetaInformation(DICOMMetaInformation metaInformation) {
        this.mMetaInformation = metaInformation;
    }

    public List<DICOMAnnotation> getAnnotations() {
        return mAnnotations;
    }

    public void setAnnotations(List<DICOMAnnotation> annotations) {
        this.mAnnotations = annotations;
    }

}
