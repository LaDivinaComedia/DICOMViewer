package be.ac.ulb.lisa.idot.dicom.data;

/**
 * DICOM file containing a meta information object (DICOMMetaInformation)
 * and a DICOM body object (DICOMBody).
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMFile {
	
	// ---------------------------------------------------------------
	// - <final> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * DICOM meta information.
	 */
	protected final DICOMMetaInformation mMetaInformation;
	
	/**
	 * DICOM body.
	 */
	protected final DICOMBody mBody;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMFile(DICOMMetaInformation metaInformation, DICOMBody body) {
		
		mMetaInformation = metaInformation;
		mBody = body;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return DICOM meta information.
	 */
	public DICOMMetaInformation getMetaInformation() {
		return mMetaInformation;
	}
	
	/**
	 * @return DICOM body.
	 */
	public DICOMBody getBody() {
		return mBody;
	}
	
	/**
	 * @return True if the file has DICOM meta information.
	 * False otherwise.
	 */
	public boolean hasMetaInformation() {
		return mMetaInformation != null;
	}
	
	/**
	 * @return True if the file has DICOM body.
	 * False otherwise.
	 */
	public boolean hasBody() {
		return mBody != null;
	}

}
