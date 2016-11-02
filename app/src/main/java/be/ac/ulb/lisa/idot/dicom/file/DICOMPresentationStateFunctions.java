package be.ac.ulb.lisa.idot.dicom.file;

import android.graphics.PointF;

import be.ac.ulb.lisa.idot.dicom.*;
import be.ac.ulb.lisa.idot.dicom.data.*;

import java.io.*;
import java.util.*;

/**
 * This is a reader of graphical annotations of DICOM presentation state files.
 *
 * @author Vladyslav Vasyliev
 */
public class DICOMPresentationStateFunctions implements DICOMReaderFunctions {
    private DICOMMetaInformationPS mMetaInformation;
    private DICOMAnnotation mAnnotation;
    private DICOMTextObject mTextObject;
    private DICOMGraphicObject mGraphicObject;
    private Dictionary<String, DICOMAnnotation> mAnnotations;

    public DICOMPresentationStateFunctions(DICOMMetaInformationPS metaInformation) {
        mAnnotations = new Hashtable<>();
        mMetaInformation = metaInformation;
    }

    public Enumeration<DICOMAnnotation> getAnnotations() {
        return mAnnotations.elements();
    }

    @Override
    public void addDICOMElement(DICOMElement parent, DICOMElement element) {
        DICOMTag dicomTag = element.getDICOMTag();
        int layerOrder;
        int tag = dicomTag.getTag();
        float[] points;
        String string;
        String msg = String.format("(%04X,%04X) '%s'", (tag & 0xffff0000) >>> 16, tag & 0xffff, dicomTag.getName());
        System.out.println(msg);
        switch (tag) {
            // Graphic Annotation Sequence
            case DICOMTag.GraphicAnnotationSequence:
                break;
            // > Graphic Layer
            case DICOMTag.GraphicLayer:
                string = element.getValueString();
                mAnnotation = mAnnotations.get(string);
                if (mAnnotation == null) {
                    mAnnotation = new DICOMAnnotation(string);
                    mAnnotations.put(string, mAnnotation);
                }
                break;
            // > Text Object Sequence
            case DICOMTag.TextObjectSequence:
                mTextObject = new DICOMTextObject();
                mAnnotation.getTextObjects().add(mTextObject);
                break;
            case DICOMTag.UnformattedTextValue:
                mTextObject.setText(element.getValue().toString());
                break;
            case DICOMTag.AnchorPoint:
                points = (float[]) element.getValue();
                mTextObject.setTextAnchor(new PointF(points[0], points[1]));
                break;
            case DICOMTag.AnchorPointVisibility:
                string = element.getValueString().trim();
                if ("Y".equals(string))
                    mTextObject.setAnchorVisibility(true);
                else
                    mTextObject.setAnchorVisibility(false);
                break;
            // > Graphic Object Sequence
            case DICOMTag.GraphicObjectSequence:
                mGraphicObject = new DICOMGraphicObject();
                mAnnotation.getGraphicObjects().add(mGraphicObject);
                break;
            case DICOMTag.GraphicAnnotationUnits:   // Should be always "PIXEL"
                break;
            case DICOMTag.GraphicDimensions:        // Should be always 2
                break;
            case DICOMTag.NumberOfGraphicPoints:
                mGraphicObject.setNumberOfGraphicPoints(element.getValueInt());
                break;
            case DICOMTag.GraphicData:
                points = (float[]) element.getValue();
                mGraphicObject.setPoints(points);
                break;
            case DICOMTag.GraphicType:
                mGraphicObject.setGraphicType(element.getValueString());
                break;
            case DICOMTag.GraphicFilled:
                string = element.getValueString().trim();
                if ("Y".equals(string))
                    mGraphicObject.setGraphicFilled(true);
                else
                    mGraphicObject.setGraphicFilled(false);
                break;

            // Graphic Layer Sequence
            case DICOMTag.GraphicLayerSequence:
                break;
            // > Graphic Layer Order
            case DICOMTag.GraphicLayerOrder:
                layerOrder = Integer.parseInt(element.getValueString().trim());
                mAnnotation.setLayerOrder(layerOrder);
                break;

            // Presentation State Description
            case DICOMTag.ContentLabel:
                mMetaInformation.setContentLabel(element.getValueString());
                break;
            case DICOMTag.ContentDescription:
                mMetaInformation.setContentDescription(element.getValueString());
                break;
            case DICOMTag.PresentationCreationDate:
                mMetaInformation.setPresentationCreationDate(element.getValueString());
                break;
            case DICOMTag.PresentationCreationTime:
                mMetaInformation.setPresentationCreationTime(element.getValueString());
                break;
            case DICOMTag.ContentCreatorsName:
                mMetaInformation.setContentCreatorsName(element.getValueString());
                break;

            // --- UNUSED ---
            // Line Style Sequence Macro Attributes
            // Line Style Sequence
            case DICOMTag.LineStyleSequence:
                break;
            // > Pattern On Color CIE Lab Value
            case DICOMTag.PatternOnColorCIELabValue:
                break;
            // > Line Thickness
            case DICOMTag.LineThickness:
                break;

            case DICOMTag.GraphicLayerDescription:
                break;
            case DICOMTag.GraphicLayerRecommendedDisplayCIELabValue:
                break;
        }
    }

    @Override
    public boolean isRequiredElement(int tag) {
        return tag == DICOMTag.GraphicAnnotationSequence
                || tag == DICOMTag.GraphicLayer
                || tag == DICOMTag.GraphicAnnotationUnits
                || tag == DICOMTag.UnformattedTextValue
                || tag == DICOMTag.TextObjectSequence
                || tag == DICOMTag.GraphicObjectSequence
                || tag == DICOMTag.AnchorPoint
                || tag == DICOMTag.AnchorPointVisibility
                || tag == DICOMTag.GraphicDimensions
                || tag == DICOMTag.NumberOfGraphicPoints
                || tag == DICOMTag.GraphicData
                || tag == DICOMTag.GraphicType
                || tag == DICOMTag.GraphicFilled
                || tag == DICOMTag.GraphicLayerSequence
                || tag == DICOMTag.GraphicLayerOrder
                || tag == DICOMTag.GraphicLayerDescription
                || tag == DICOMTag.LineStyleSequence
                || tag == DICOMTag.PatternOnColorCIELabValue
                || tag == DICOMTag.LineThickness
                || tag == DICOMTag.GraphicLayerRecommendedDisplayCIELabValue
                // Presentation State Description
                || tag == DICOMTag.ContentLabel
                || tag == DICOMTag.ContentDescription
                || tag == DICOMTag.PresentationCreationDate
                || tag == DICOMTag.PresentationCreationTime
                || tag == DICOMTag.ContentCreatorsName

                || tag == DICOMTag.Modality;
    }

    @Override
    public void computeImage(DICOMElement parent, DICOMValueRepresentation VR, long valueLength)
            throws IOException, EOFException, DICOMException {
        throw new IOException("PixelData in Presentation State file.");
    }
}
