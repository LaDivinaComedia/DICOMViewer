package be.ac.ulb.lisa.idot.dicom.data;

import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

public class DICOMImage extends DICOMFile {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Unknown image status.
	 */
	public static final short UNKNOWN_STATUS = 0;
	
	/**
	 * Uncompressed image status.
	 */
	public static final short UNCOMPRESSED = 1;
	
	/**
	 * Compressed image status. 
	 */
	public static final short COMPRESSED = 2;
	
	
	// ---------------------------------------------------------------
	// - <final> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * LISA 16-Bit grayscale image.
	 */
	private final LISAImageGray16Bit mImage;
	
	/**
	 * The compression status.
	 */
	private final short mCompressionStatus;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMImage(DICOMMetaInformation metaInformation, DICOMBody body,
			LISAImageGray16Bit image, short compressionStatus) {
		
		super(metaInformation, body);
		
		mImage = image;
		mCompressionStatus = compressionStatus;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return DICOM image.
	 */
	public LISAImageGray16Bit getImage() {
		return mImage;
	}
	
	/**
	 * @return Compression status.
	 */
	public short getCompressionStatus() {
		return mCompressionStatus;
	}
	
	/**
	 * @return Check if the image is uncompressed.
	 */
	public boolean isUncompressed() {
		return mCompressionStatus == UNCOMPRESSED;
	}
	
	/**
	 * @return Check if the image as data.
	 */
	public boolean hasImageData() {
		
		if (mImage == null)
			return false;
		
		return mImage.getData() != null;
	}
	
}
