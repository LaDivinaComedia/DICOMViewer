package be.ac.ulb.lisa.idot.dicom;

import java.util.ArrayList;
import java.util.List;


/**
 * DICOM sequence.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMSequence extends DICOMElement {
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------

	/**
	 * List of DICOMElement children (normally DICOMItem).
	 */
	protected List<DICOMElement> mChildrenList;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMSequence(DICOMTag dicomTag) {
		super(dicomTag, null);
		
		mChildrenList = new ArrayList<DICOMElement>();
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Add a DICOMElement child to the sequence (List).
	 * @param element
	 */
	public void addChild(DICOMElement element) {
		mChildrenList.add(element);
	}
	
	/**
	 * Get a DICOMElement child from the List correspond to the index.
	 * @param index Index of the child.
	 * @return DICOMElement child.
	 * @throws IndexOutOfBoundsException
	 */
	public DICOMElement getChild(int index) throws IndexOutOfBoundsException {
		return mChildrenList.get(index);
	}
	
	/**
	 * @return DICOMElement children List.
	 */
	public List<DICOMElement> getChildrenList() {
		return mChildrenList;
	}
	
	/**
	 * @return Number of children in the List.
	 */
	public int getChildrenCount() {
		return mChildrenList.size();
	}

}
