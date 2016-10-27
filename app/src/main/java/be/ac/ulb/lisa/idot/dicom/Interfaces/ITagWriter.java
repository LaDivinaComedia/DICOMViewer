package be.ac.ulb.lisa.idot.dicom.Interfaces;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;

/**
 * Created by Iggytoto on 27.10.2016.
 */

public interface ITagWriter {
    void openDICM(String filename);
    void writeTag(DICOMTag t,byte[] v);
}
