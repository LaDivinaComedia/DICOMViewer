package be.ac.ulb.lisa.idot.dicom.file;

import java.io.EOFException;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;

/**
 * Interface for DICOM Reader.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public interface DICOMReaderFunctions {
	
	/**
	 * Add the DICOM element to an object (e.g. DICOMBody)
	 * or to the parent.
	 * 
	 * @param parent Parent if it is a sequence. 
	 * @param element Element to add.
	 */
	void addDICOMElement(DICOMElement parent, DICOMElement element);
	
	/**
	 * Check if the DICOM element is required for DICOMTag integer value
	 * tag.
	 * 
	 * @param tag Integer value of the DICOMTag to check.
	 * @return
	 */
	boolean isRequiredElement(int tag);
	
	
	/**
	 * Compute the image.
	 * 
	 * @param parent Parent if it is a sequence.
	 * @param VR DICOM value representation of the value.
	 * @param valueLength Length of the value.
	 */
	void computeImage(DICOMElement parent, DICOMValueRepresentation VR, long valueLength)
		throws IOException, EOFException, DICOMException;

}
