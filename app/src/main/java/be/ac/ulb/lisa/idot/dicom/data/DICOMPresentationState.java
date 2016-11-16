package be.ac.ulb.lisa.idot.dicom.data;

import java.util.ArrayList;
import java.util.List;

import be.ac.ulb.lisa.idot.dicom.CloneFactory;

/**
 * This file represent DICOM Presentation State file.
 * It includes information about the annotations, styles which are used
 * to display them, etc.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMPresentationState extends DICOMFile {
    // List of the annotations of the specified file.
    private List<DICOMAnnotation> mAnnotations = new ArrayList<>();

    protected static DICOMBody createBody(DICOMImage image) {
        DICOMBody body = (DICOMBody) CloneFactory.deepClone(image.getBody());
        body.setModality("PS");
        return body;
    }

    public DICOMPresentationState(DICOMImage image) {
        super((DICOMMetaInformation) CloneFactory.deepClone(image.getMetaInformation()),
                createBody(image));
    }

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
