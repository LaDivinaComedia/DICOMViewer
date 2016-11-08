package be.ac.ulb.lisa.idot.dicom.Interfaces;

import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;

/**
 * Created by Iggytoto on 27.10.2016.
 */

public interface ITagWriter {
    void writeTag(DICOMTag t,byte[] v) throws IOException, DICOMException;
}
