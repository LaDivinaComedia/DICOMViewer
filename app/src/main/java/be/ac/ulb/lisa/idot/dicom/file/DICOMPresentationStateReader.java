package be.ac.ulb.lisa.idot.dicom.file;

import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformationPS;
import be.ac.ulb.lisa.idot.dicom.data.DICOMPresentationState;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * DICOM Presentation State File Eeader.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMPresentationStateReader extends DICOMReader {

    public DICOMPresentationStateReader(File file) throws FileNotFoundException {
        super(file);
    }

    public DICOMPresentationStateReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public final DICOMPresentationState parse() throws IOException, EOFException, DICOMException {
        // Variables declaration
        DICOMMetaInformationPS metaInformation = null;
        boolean isExplicit;
        // Parse meta information
        if (hasMetaInformation()) {
            metaInformation = new DICOMMetaInformationPS(parseMetaInformation());
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
        DICOMPresentationStateFunctions readerFunctions = new DICOMPresentationStateFunctions(metaInformation);
        parse(null, 0xffffffffL, isExplicit, readerFunctions, true);
        List<DICOMAnnotation> annotations = Collections.list(readerFunctions.getAnnotations());
        Collections.sort(annotations, new Comparator<DICOMAnnotation>() {
            @Override
            public int compare(DICOMAnnotation o1, DICOMAnnotation o2) {
                return o1.getLayerOrder() - o2.getLayerOrder();
            }
        });
        return new DICOMPresentationState(metaInformation, readerFunctions.getBody(), annotations,
                mFileName);
    }

}
