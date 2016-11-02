package be.ac.ulb.lisa.idot.dicom.data;

/**
 * DICOM Body.
 *
 * @author Vladyslav Vasyliev
 * @author Pierre Malarme
 * @version 1.1
 */
public class DICOMBody {

	private String mSpecificCharset = "ASCII";		// DICOMTag.SpecificCharacterSet
	private String mImageType = "";					// DICOMTag.ImageType
    private String mSOPClassUID = "";               // DICOMTag.SOPClassUID
    private String mSOPInstanceUID = "";            // DICOMTag.SOPInstanceUID
    private String mStudyDate = "";                 // DICOMTag.StudyDate
    // DICOMTag.SeriesDate
    private String mStudyTime = "";                 // DICOMTag.StudyTime
    // DICOMTag.ContentTime
    // DICOMTag.AccessionNumber
	private String mModality = "";					// DICOMTag.Modality
    private String mReferringPhysicianName = "";    // DICOMTag.ReferringPhysiciansName
    private String mStationName = "";               // DICOMTag.StationName
    private String mStudyDescription = "";          // DICOMTag.StudyDescription
    // DICOMTag.SeriesDescription
    // DICOMTag.CodingSchemeName
    // DICOMTag.ReferencedSeriesSequence
    // DICOMTag.ReferencedImageSequence

    private int mSamplesPerPixel = 1;               // DICOMTag.SamplesPerPixel
    private int mBitsAllocated = 0;                 // DICOMTag.BitsAllocated
    private int mBitsStored = 0;                    // DICOMTag.BitsStored
    private int mHightBit = 0;                      // DICOMTag.HighBit
    private int mPixelRepresentation = 1;           // DICOMTag.PixelRepresentation

	private String mManufacturer = "";
	private String mInstitutionName = "";
	private String mDepartmenName = "";
	private String mManufacturerModelName = "";
	private String mPatientName = "";
	private String mPatientId = "";
	private String mProtocolName = "";
	private String mPatientPosition = "";
	private int mStudyId = -1;
	private int mSeriesNumber = -1;
	private int mInstanceNumber = -1;
	private String mPhotometricInterpretation = "MONOCHROME2";
	private String mRequestingPhysician = "";
	private String mRequestedProcedureDescription = "";
	private String mScheduledProcedureStepDescription = "";


	public DICOMBody() { }

	public String getSpecificCharset() {
		return mSpecificCharset;
	}

    public void setSpecificCharset(String mSpecificCharset) {
        this.mSpecificCharset = mSpecificCharset;
    }

    public String getImageType() {
        return mImageType;
    }

    public void setImageType(String mImageType) {
        this.mImageType = mImageType;
    }

    public String getSOPClassUID() {
        return mSOPClassUID;
    }

    public void setSOPClassUID(String mSOPClassUID) {
        this.mSOPClassUID = mSOPClassUID;
    }

    public String getSOPInstanceUID() {
        return mSOPInstanceUID;
    }

    public void setSOPInstanceUID(String mSOPInstanceUID) {
        this.mSOPInstanceUID = mSOPInstanceUID;
    }

	public String getStudyDate() {
		return mStudyDate;
	}

    public void setStudyDate(String mStudyDate) {
        this.mStudyDate = mStudyDate;
    }

    public String getStudyTime() {
        return mStudyTime;
    }

    public void setStudyTime(String mStudyTime) {
        this.mStudyTime = mStudyTime;
    }

    public String getModality() {
        return mModality;
    }

    public void setModality(String mModality) {
        this.mModality = mModality;
    }

    public String getReferringPhysicianName() {
        return mReferringPhysicianName;
    }

    public void setReferringPhysicianName(String mReferringPhysicianName) {
        this.mReferringPhysicianName = mReferringPhysicianName;
    }

    public String getStationName() {
        return mStationName;
    }

    public void setStationName(String mStationName) {
        this.mStationName = mStationName;
    }

    public String getStudyDescription() {
        return mStudyDescription;
    }

    public void setStudyDescription(String mStudyDescription) {
        this.mStudyDescription = mStudyDescription;
    }

    public int getSamplesPerPixel() {
        return mSamplesPerPixel;
    }

    public void setSamplesPerPixel(int mSamplesPerPixel) {
        this.mSamplesPerPixel = mSamplesPerPixel;
    }

    public int getBitsAllocated() {
        return mBitsAllocated;
    }

    public void setBitsAllocated(int mBitsAllocated) {
        this.mBitsAllocated = mBitsAllocated;
    }

    public int getBitsStored() {
        return mBitsStored;
    }

    public void setBitsStored(int mBitsStored) {
        this.mBitsStored = mBitsStored;
    }

    public int getHightBit() {
        return mHightBit;
    }

    public void setHightBit(int mHightBit) {
        this.mHightBit = mHightBit;
    }

    public int getPixelRepresentation() {
        return mPixelRepresentation;
    }

    public void setPixelRepresentation(int mPixelRepresentation) {
        this.mPixelRepresentation = mPixelRepresentation;
    }



	public String getManufacturer() {
		return mManufacturer;
	}

	public String getInstitutionName() {
		return mInstitutionName;
	}

	public String getDepartmenName() {
		return mDepartmenName;
	}

	public String getManufacturerModelName() {
		return mManufacturerModelName;
	}

	public String getPatientName() {
		return mPatientName;
	}

	public String getPatientId() {
		return mPatientId;
	}

	public String getProtocolName() {
		return mProtocolName;
	}

	public String getPatientPosition() {
		return mPatientPosition;
	}

	public int getStudyId() {
		return mStudyId;
	}

	public int getSeriesNumber() {
		return mSeriesNumber;
	}

	public int getInstanceNumber() {
		return mInstanceNumber;
	}

	public String getPhotometricInterpretation() {
		return mPhotometricInterpretation;
	}

	public String getRequestingPhysician() {
		return mRequestingPhysician;
	}

	public String getRequestedProcedureDescription() {
		return mRequestedProcedureDescription;
	}

	public String getScheduledProcedureStepDescription() {
		return mScheduledProcedureStepDescription;
	}

	public void setManufacturer(String mManufacturer) {
		this.mManufacturer = mManufacturer;
	}

	public void setInstitutionName(String mInstitutionName) {
		this.mInstitutionName = mInstitutionName;
	}

	public void setDepartmenName(String mDepartmenName) {
		this.mDepartmenName = mDepartmenName;
	}

	public void setManufacturerModelName(String mManufacturerModelName) {
		this.mManufacturerModelName = mManufacturerModelName;
	}

	public void setPatientName(String mPatientName) {
		this.mPatientName = mPatientName;
	}

	public void setPatientId(String mPatientId) {
		this.mPatientId = mPatientId;
	}

	public void setProtocolName(String mProtocolName) {
		this.mProtocolName = mProtocolName;
	}

	public void setPatientPosition(String mPatientPosition) {
		this.mPatientPosition = mPatientPosition;
	}

	public void setStudyId(int mStudyId) {
		this.mStudyId = mStudyId;
	}

	public void setSeriesNumber(int mSeriesNumber) {
		this.mSeriesNumber = mSeriesNumber;
	}

	public void setInstanceNumber(int mInstanceNumber) {
		this.mInstanceNumber = mInstanceNumber;
	}

	public void setPhotometricInterpretation(String mPhotometricInterpretation) {
		this.mPhotometricInterpretation = mPhotometricInterpretation;
	}

	public void setRequestingPhysician(String mRequestingPhysician) {
		this.mRequestingPhysician = mRequestingPhysician;
	}

	public void setRequestedProcedureDescription(
			String mRequestedProcedureDescription) {
		this.mRequestedProcedureDescription = mRequestedProcedureDescription;
	}

	public void setScheduledProcedureStepDescription(
			String mScheduledProcedureStepDescription) {
		this.mScheduledProcedureStepDescription = mScheduledProcedureStepDescription;
	}

}
