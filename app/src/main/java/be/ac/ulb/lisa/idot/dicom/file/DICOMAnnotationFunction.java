package be.ac.ulb.lisa.idot.dicom.file;

import be.ac.ulb.lisa.idot.dicom.*;

import java.io.*;

/**
 * Created by vlad on 19.10.2016.
 */
public class DICOMAnnotationFunction implements DICOMReaderFunctions {
    @Override
    public void addDICOMElement(DICOMElement parent, DICOMElement element) {

    }

    @Override
    public boolean isRequiredElement(int tag) {
        return (tag == DICOMTag.GraphicAnnotationSequence)
                || (tag == DICOMTag.GraphicLayer)
                || (tag == DICOMTag.GraphicAnnotationUnits)
                || (tag == DICOMTag.TextObjectSequence)
                || (tag == DICOMTag.GraphicObjectSequence)
                || (tag == DICOMTag.AnchorPoint)
                || (tag == DICOMTag.GraphicDimensions)
                || (tag == DICOMTag.NumberOfGraphicPoints)
                || (tag == DICOMTag.GraphicData)
                || (tag == DICOMTag.GraphicType)
                || (tag == DICOMTag.GraphicFilled)
                || (tag == DICOMTag.GraphicLayerSequence)
                || (tag == DICOMTag.GraphicLayerOrder)
                || (tag == DICOMTag.GraphicLayerDescription)
                || (tag == DICOMTag.ContentDescription)
                || (tag == DICOMTag.PresentationCreationDate)
                || (tag == DICOMTag.PresentationCreationTime)
                || (tag == DICOMTag.ContentCreatorsName)
                || (tag == DICOMTag.LineStyleSequence)
                || (tag == DICOMTag.PatternOnColorCIELabValue)
                || (tag == DICOMTag.LineThickness)
                || (tag == DICOMTag.GraphicLayerRecommendedDisplayCIELabValue);
    }

    @Override
    public void computeImage(DICOMElement parent, DICOMValueRepresentation VR, long valueLength)
            throws IOException, EOFException, DICOMException {

    }
}
