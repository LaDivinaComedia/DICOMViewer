package be.ac.ulb.lisa.idot.dicom.file;

import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by vlad on 02.11.2016.
 */
public class DICOMAnnotationReader extends DICOMReader {
    public DICOMAnnotationReader(File file) throws FileNotFoundException {
        super(file);
    }

    public DICOMAnnotationReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public final void parse() throws IOException, EOFException, DICOMException {
        // Variables declaration
        DICOMMetaInformation metaInformation = null;
        boolean isExplicit;
        // Parse meta information
        if (hasMetaInformation()) {
            metaInformation = parseMetaInformation();
            String transferSyntaxUID = metaInformation.getTransferSyntaxUID();
            if (transferSyntaxUID.equals("1.2.840.10008.1.2")) {
                isExplicit = false;
                setByteOrder(LITTLE_ENDIAN);
            } else if (transferSyntaxUID.equals("1.2.840.10008.1.2.1")) {
                isExplicit = true;
                setByteOrder(LITTLE_ENDIAN);
            } else if (transferSyntaxUID.equals("1.2.840.10008.1.2.2")) {
                isExplicit = true;
                setByteOrder(BIG_ENDIAN);
            } else {
                // Compressed image are not supported yet => throw a exception
                throw new DICOMException("The image is compressed."
                        + " This is not supported yet.");
            }
        } else {
            isExplicit = false;
            setByteOrder(LITTLE_ENDIAN);
        }
        // Parse the body
        DICOMAnnotationFunctions readerFunctions = new DICOMAnnotationFunctions();
        parse(null, 0xffffffffL, isExplicit, readerFunctions, true);
        List<DICOMAnnotation> annotations = Collections.list(readerFunctions.getAnnotations());
        System.out.println(annotations);
    }

}
