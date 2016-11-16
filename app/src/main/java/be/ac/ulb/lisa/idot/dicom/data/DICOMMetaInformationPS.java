package be.ac.ulb.lisa.idot.dicom.data;

/**
 * DICOM Presentation State Meta Information.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMMetaInformationPS extends DICOMMetaInformation {
    protected String mContentLabel = "";
    protected String mContentDescription = "";
    protected String mPresentationCreationDate = "";
    protected String mPresentationCreationTime = "";
    protected String mContentCreatorsName = "";

    protected String LineStyleSequence         = "";
    protected String PatternOnColorCIELabValue = "";
    protected String LineThickness             = "";
    protected String GraphicLayerRecommendedDisplayCIELabValue = "";

    public DICOMMetaInformationPS(){}

    public DICOMMetaInformationPS(DICOMMetaInformation metaInformation) {
        mGroupLength = metaInformation.mGroupLength;
        mFileMetaInformationVersion = metaInformation.mFileMetaInformationVersion;
        mSOPClassUID = metaInformation.mSOPClassUID;
        mSOPInstanceUID = metaInformation.mSOPInstanceUID;
        mTransferSyntaxUID = metaInformation.mTransferSyntaxUID;
        mImplementationClassUID = metaInformation.mImplementationClassUID;
        mImplementationVersionName = metaInformation.mImplementationVersionName;
        mAET = metaInformation.mAET;
        mPatientBirthDate = metaInformation.mPatientBirthDate;
        mPatientName = metaInformation.mPatientName;
        mPatientAge = metaInformation.mPatientAge;
        mPixelSpacing = metaInformation.mPixelSpacing;
    }

    public String getContentLabel() {
        return mContentLabel;
    }

    public void setContentLabel(String contentLabel) {
        mContentLabel = contentLabel;
    }

    public String getContentDescription() {
        return mContentDescription;
    }

    public void setContentDescription(String contentDescription) {
        mContentDescription = contentDescription;
    }

    public String getPresentationCreationDate() {
        return mPresentationCreationDate;
    }

    public void setPresentationCreationDate(String presentationCreationDate) {
        mPresentationCreationDate = presentationCreationDate;
    }

    public String getPresentationCreationTime() {
        return mPresentationCreationTime;
    }

    public void setPresentationCreationTime(String presentationCreationTime) {
        mPresentationCreationTime = presentationCreationTime;
    }

    public String getContentCreatorsName() {
        return mContentCreatorsName;
    }

    public void setContentCreatorsName(String contentCreatorsName) {
        mContentCreatorsName = contentCreatorsName;
    }

}
