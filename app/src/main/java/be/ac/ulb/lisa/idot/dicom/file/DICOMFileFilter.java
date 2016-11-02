package be.ac.ulb.lisa.idot.dicom.file;

import java.io.File;
import java.io.FileFilter;
/**
 * Filter the file on the basis of their extension.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMFileFilter implements FileFilter {
	
	// ---------------------------------------------------------------
	// + FUNCTION
	// ---------------------------------------------------------------

	// TODO check the DICOM meta information ?
	// This can lead to out of memory issue or
	// be very slow.
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		
		if (pathname.isFile() && !pathname.isHidden()) {
			
			// Get the file name
			String fileName = pathname.getName();
			
			// If the file is a DICOMDIR return false
			if (fileName.equals("DICOMDIR"))
				return false;
			
			// Get the dot index
			int dotIndex = fileName.lastIndexOf(".");
			
			// If the dotIndex is equal to -1 this is
			// a file without extension has are the DICOM
			// files
			if (dotIndex == -1)
				return true;
			
			// Check the file extension
			String fileExtension = fileName.substring(dotIndex + 1);
			
			if (fileExtension.equalsIgnoreCase("dcm"))
				return true;
			
		}
		
		return false;
	}
	
}
