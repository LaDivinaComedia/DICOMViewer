package be.ac.ulb.lisa.idot.dicom.data;

import java.util.List;

/**
 * This file represent DICOM Presentation State file.
 * It includes information about the annotations, styles which are used
 * to display them, etc.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMPresentationState extends DICOMFile {
    // List of the annotations of the specified file.
    private List<DICOMAnnotation> mAnnotations;

    public DICOMPresentationState(DICOMMetaInformationPS metaInformation, DICOMBody body) {
        super(metaInformation, body);
    }

    public DICOMPresentationState(DICOMMetaInformationPS metaInformation, DICOMBody body,
                                  List<DICOMAnnotation> annotations) {
        super(metaInformation, body);
        mAnnotations = annotations;
    }

    @Override
    public DICOMMetaInformationPS getMetaInformation() {
        return (DICOMMetaInformationPS) mMetaInformation;
    }

    public List<DICOMAnnotation> getAnnotations() {
        return mAnnotations;
    }

    public void setAnnotations(List<DICOMAnnotation> annotations) {
        this.mAnnotations = annotations;
    }

}
