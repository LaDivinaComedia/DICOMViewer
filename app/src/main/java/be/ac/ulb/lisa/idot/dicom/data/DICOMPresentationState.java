package be.ac.ulb.lisa.idot.dicom.data;

import java.io.IOException;
import java.util.List;

import be.ac.ulb.lisa.idot.dicom.file.DICOMAnnotationWriter;

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

    public DICOMPresentationState(DICOMMetaInformationPS metaInformation, DICOMBody body,String fileName) {
        super(metaInformation, body, fileName);
    }

    public DICOMPresentationState(DICOMMetaInformationPS metaInformation, DICOMBody body,
                                  List<DICOMAnnotation> annotations, String fileName) {
        super(metaInformation, body, fileName);
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

    /**
     * Saves annotations to adjacent file *.ps
     */
    public void saveAnnotations() throws IOException {
        DICOMAnnotationWriter writer = new DICOMAnnotationWriter();
        writer.writeAnnotations(this,mFileName);
    }

}
