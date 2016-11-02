package be.ac.ulb.lisa.idot.dicom.data;

public class DICOMMetaInformation {
    private long mGroupLength = -1;
    private String mSOPClassUID = "";
    private String mSOPInstanceUID = "";
    private String mTransferSyntaxUID = "";
    private String mImplementationClassUID = "";
    private String mImplementationVersionName = "";
    private String mAET = "";                            // Application Entity Title.
    private String mPatientBirthDate = "";
    private String mPatientName = "";
    private String mPatientAge = "";
    private double[] pixelSpacing;

    /**
     * @return the mGroupLength
     */
    public long getGroupLength() {
        return mGroupLength;
    }

    /**
     * @return the mSOPClassUID
     */
    public String getSOPClassUID() {
        return mSOPClassUID;
    }

    /**
     * @return the mSOPInstanceUID
     */
    public String getSOPInstanceUID() {
        return mSOPInstanceUID;
    }

    /**
     * @return the mTransferSyntaxUID
     */
    public String getTransferSyntaxUID() {
        return mTransferSyntaxUID;
    }

    /**
     * @return the mImplementationClassUID
     */
    public String getImplementationClassUID() {
        return mImplementationClassUID;
    }

    /**
     * @return the mImplementationVersionName
     */
    public String getImplementationVersionName() {
        return mImplementationVersionName;
    }

    /**
     * @return the mAET
     */
    public String getAET() {
        return mAET;
    }

    /* @return the Patient Birth date
    */
    public String getPatientBirthDate() {
        return mPatientBirthDate;
    }

    /**
     * @return patients name
     */
    public String getPatientName() {
        return mPatientName;
    }

    /**
     * @return patients age
     */
    public String getPatientAge() {
        return mPatientAge;
    }

    /**
     * @param mGroupLength the mGroupLength to set
     */
    public void setGroupLength(long mGroupLength) {
        this.mGroupLength = mGroupLength;
    }

    /**
     * @param mSOPClassUID the mSOPClassUID to set
     */
    public void setSOPClassUID(String mSOPClassUID) {
        this.mSOPClassUID = mSOPClassUID;
    }

    /**
     * @param mSOPInstanceUID the mSOPInstanceUID to set
     */
    public void setSOPInstanceUID(String mSOPInstanceUID) {
        this.mSOPInstanceUID = mSOPInstanceUID;
    }

    /**
     * @param mTransferSyntaxUID the mTransferSyntaxUID to set
     */
    public void setTransferSyntaxUID(String mTransferSyntaxUID) {
        this.mTransferSyntaxUID = mTransferSyntaxUID;
    }

    /**
     * @param mImplementationClassUID the mImplementationClassUID to set
     */
    public void setImplementationClassUID(String mImplementationClassUID) {
        this.mImplementationClassUID = mImplementationClassUID;
    }

    /**
     * @param mImplementationVersionName the mImplementationVersionName to set
     */
    public void setImplementationVersionName(String mImplementationVersionName) {
        this.mImplementationVersionName = mImplementationVersionName;
    }

    /**
     * @param mAET the mAET to set
     */
    public void setAET(String mAET) {
        this.mAET = mAET;
    }

    /**
     * @param mPatientBirthDate the mImplementationPatientBirthDate to set
     */
    public void setmPatientBirthDate(String mPatientBirthDate) {
        this.mPatientBirthDate = mPatientBirthDate;
    }

    /**
     * @param patientName the mImplementationPatientName to set
     */
    public void setPatientName(String patientName) {
        this.mPatientName = patientName;
    }

    /**
     * @param patientName the mImplementationPatientAge to set
     */
    public void setPatientAge(String patientName) {
        this.mPatientAge = patientName;
    }

    public void setPixelSpacing(double[] pixelSpacing) {
        this.pixelSpacing = pixelSpacing;
    }

    public double[] getPixelSpacing() {
        return pixelSpacing;
    }
}
