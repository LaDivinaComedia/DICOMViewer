/*
*
* Copyright (C) 2011 Pierre Malarme
*
* Authors: Pierre Malarme <pmalarme at ulb.ac.be>
*
* Institution: Laboratory of Image Synthesis and Analysis (LISA)
*              Faculty of Applied Science
*              Universite Libre de Bruxelles (U.L.B.)
*
* Website: http://lisa.ulb.ac.be
*
* This file <DICOMFileFilter.java> is part of Droid Dicom Viewer.
*
* Droid Dicom Viewer is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Droid Dicom Viewer is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Droid Dicom Viewer. If not, see <http://www.gnu.org/licenses/>.
*
* Released date: 17-02-2011
*
* Version: 1.0
*
*/

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
