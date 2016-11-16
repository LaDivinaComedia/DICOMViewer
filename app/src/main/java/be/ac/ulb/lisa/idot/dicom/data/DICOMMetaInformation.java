package be.ac.ulb.lisa.idot.dicom.data;

/**
 * DICOM Meta Information.
 *
 * @author Pierre Malarme
 * @author Vladyslav Vasyliev
 * @version 1.2
 */
public class DICOMMetaInformation {
    protected long mGroupLength = -1;                     // DICOMTag.FileMetaInformationGroupLength
    protected String mFileMetaInformationVersion = "";    // DICOMTag.FileMetaInformationVersion
    protected String mSOPClassUID = "";                   // DICOMTag.MediaStorageSOPClassUID
    protected String mSOPInstanceUID = "";                // DICOMTag.MediaStorageSOPInstanceUID
    protected String mTransferSyntaxUID = "";             // DICOMTag.TransferSyntaxUID
    protected String mImplementationClassUID = "";        // DICOMTag.ImplementationClassUID
    protected String mImplementationVersionName = "";     // DICOMTag.ImplementationVersionName
    protected String mAET = "";                           // DICOMTag.SourceApplicationEntity
//    protected String mPrivateInformationCreatorUID = "";  // DICOMTag.PrivateInformationCreatorUID

    protected String mPatientBirthDate = "";
    protected String mPatientName = "";
    protected String mPatientAge = "";
    protected float[] mPixelSpacing;

    public DICOMMetaInformation() {
        // Set default value for the pixel spacing
        mPixelSpacing = new float[] { 1.0f, 1.0f };
    }

    public long getGroupLength() {
        return mGroupLength;
    }

    public String getFileMetaInformationVersion() {
        return mFileMetaInformationVersion;
    }

    public String getSOPClassUID() {
        return mSOPClassUID;
    }

    public String getSOPInstanceUID() {
        return mSOPInstanceUID;
    }

    public String getTransferSyntaxUID() {
        return mTransferSyntaxUID;
    }

    public String getImplementationClassUID() {
        return mImplementationClassUID;
    }

    public String getImplementationVersionName() {
        return mImplementationVersionName;
    }

    public String getAET() {
        return mAET;
    }

    public String getPatientBirthDate() {
        return mPatientBirthDate;
    }

    public String getPatientName() {
        return mPatientName;
    }

    public String getPatientAge() {
        return mPatientAge;
    }

    public float[] getPixelSpacing() {
        return mPixelSpacing;
    }


    public void setGroupLength(long mGroupLength) {
        this.mGroupLength = mGroupLength;
    }

    public void setFileMetaInformationVersion(String mFileMetaInformationVersion) {
        this.mFileMetaInformationVersion = mFileMetaInformationVersion;
        if(mFileMetaInformationVersion == "0/1"){
            this.mFileMetaInformationVersion = "1";
        }
    }

    public void setSOPClassUID(String mSOPClassUID) {
        this.mSOPClassUID = mSOPClassUID;
    }

    public void setSOPInstanceUID(String mSOPInstanceUID) {
        this.mSOPInstanceUID = mSOPInstanceUID;
    }

    public void setTransferSyntaxUID(String mTransferSyntaxUID) {
        this.mTransferSyntaxUID = mTransferSyntaxUID;
    }

    public void setImplementationClassUID(String mImplementationClassUID) {
        this.mImplementationClassUID = mImplementationClassUID;
    }

    public void setImplementationVersionName(String mImplementationVersionName) {
        this.mImplementationVersionName = mImplementationVersionName;
    }

    public void setAET(String mAET) {
        this.mAET = mAET;
    }

    public void setPatientBirthDate(String mPatientBirthDate) {
        this.mPatientBirthDate = mPatientBirthDate;
    }

    public void setPatientName(String patientName) {
        this.mPatientName = patientName;
    }

    public void setPatientAge(String patientName) {
        this.mPatientAge = patientName;
    }

    public void setPixelSpacing(float[] pixelSpacing) {
        this.mPixelSpacing = pixelSpacing;
    }

}
