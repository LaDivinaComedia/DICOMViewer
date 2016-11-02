package be.ac.ulb.lisa.idot.dicom;

import java.util.HashMap;
import java.util.Map;

/**
 * DICOM item.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMItem extends DICOMElement {
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * The map of DICOMElement children.
	 */
	protected Map<Integer, DICOMElement> mChildrenMap;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMItem() {
		super(DICOMTag.createDICOMTag(0xfffee000), null);
		
		mChildrenMap = new HashMap<Integer, DICOMElement>();
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Add a DICOMElement child to the map.
	 * 
	 * @param tag
	 * @param element
	 */
	public void addChild(int tag, DICOMElement element) {
		mChildrenMap.put(tag, element);
	}
	
	/**
	 * Get a DICOMElement from the map.
	 * 
	 * @param tag The tag integer value of the child.
	 * @return DICOMElement or null if it does
	 * not exist.
	 */
	public DICOMElement getChild(int tag) {
		return mChildrenMap.get(tag);
	}
	
	/**
	 * @return DICOMElement children map.
	 */
	public Map<Integer, DICOMElement> getChildrenMap() {
		return mChildrenMap;
	}
	
	/**
	 * @return Number of DICOMElement children.
	 */
	public int getChildrenCount() {
		return mChildrenMap.size();
	}
	
	/**
	 * @param tag
	 * @return True if there is a child with is DICOMTag
	 * integer value set as tag. False otherwise.
	 */
	public boolean containsChild(int tag) {
		return mChildrenMap.containsKey(tag);
	}

}
