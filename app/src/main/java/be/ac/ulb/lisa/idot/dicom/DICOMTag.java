package be.ac.ulb.lisa.idot.dicom;

import java.util.HashMap;
import java.util.Map;

/**
 * DICOM tag.
 *
 * @author Vladyslav Vasyliev
 * @author Pierre Malarme
 * @version 1.2
 */
public class DICOMTag {
    public static final int FileMetaInformationGroupLength      = 0x0002_0000;
    public static final int FileMetaInformationVersion          = 0x0002_0001;
    public static final int MediaStorageSOPClassUID             = 0x0002_0002;
    public static final int MediaStorageSOPInstanceUID          = 0x0002_0003;
    public static final int TransferSyntaxUID                   = 0x0002_0010;
    public static final int ImplementationClassUID              = 0x0002_0012;
    public static final int ImplementationVersionName           = 0x0002_0013;
    public static final int SourceApplicationEntity             = 0x0002_0016;
    public static final int PrivateInformationCreatorUID        = 0x0002_0100;
    public static final int PrivateInformation                  = 0x0002_0102;

    public static final int SpecificCharacterSet                = 0x0008_0005;
    public static final int ImageType                           = 0x0008_0008;
    public static final int Modality                            = 0x0008_0060;
    public static final int CodingSchemeName                    = 0x0008_0115;
    public static final int ReferencedSeriesSequence            = 0x0008_1115;
    public static final int ReferencedImageSequence             = 0x0008_1140;

    public static final int PatientsName                        = 0x0010_0010;
    public static final int PatientsBirthDate                   = 0x0010_0030;
    public static final int PatientsAge                         = 0x0010_1010;

    public static final int ImageOrientationPatient             = 0x0020_0037;

    public static final int SamplesPerPixel                     = 0x0028_0002;
    public static final int Rows                                = 0x0028_0010;
    public static final int Columns                             = 0x0028_0011;
    public static final int PixelSpacing                        = 0x0028_0030;
    public static final int BitsAllocated                       = 0x0028_0100;
    public static final int BitsStored                          = 0x0028_0101;
    public static final int HighBit                             = 0x0028_0102;
    public static final int PixelRepresentation                 = 0x0028_0103;
    public static final int WindowCenter                        = 0x0028_1050;
    public static final int WindowWidth                         = 0x0028_1051;

    public static final int GraphicAnnotationSequence           = 0x0070_0001;
    public static final int GraphicLayer                        = 0x0070_0002;
    public static final int GraphicAnnotationUnits              = 0x0070_0005;
    public static final int UnformattedTextValue                = 0x0070_0006;
    public static final int TextObjectSequence                  = 0x0070_0008;
    public static final int GraphicObjectSequence               = 0x0070_0009;
    public static final int AnchorPoint                         = 0x0070_0014;
    public static final int AnchorPointVisibility               = 0x0070_0015;
    public static final int GraphicDimensions                   = 0x0070_0020;
    public static final int NumberOfGraphicPoints               = 0x0070_0021;
    public static final int GraphicData                         = 0x0070_0022;
    public static final int GraphicType                         = 0x0070_0023;
    public static final int GraphicFilled                       = 0x0070_0024;
    public static final int GraphicLayerSequence                = 0x0070_0060;
    public static final int GraphicLayerOrder                   = 0x0070_0062;
    public static final int GraphicLayerDescription             = 0x0070_0068;
    public static final int ContentDescription                  = 0x0070_0081;
    public static final int PresentationCreationDate            = 0x0070_0082;
    public static final int PresentationCreationTime            = 0x0070_0083;
    public static final int ContentCreatorsName                 = 0x0070_0084;
    public static final int LineStyleSequence                   = 0x0070_0232;
    public static final int PatternOnColorCIELabValue           = 0x0070_0251;
    public static final int LineThickness                       = 0x0070_0253;
    public static final int GraphicLayerRecommendedDisplayCIELabValue = 0x0070_0401;

    public static final int PixelData                           = 0x7fe0_0010;

    public static final int Item                                = 0xfffe_e000;
    public static final int ItemDelimitationTag                 = 0xfffe_e00d;
    public static final int SequenceDelimitationTag             = 0xfffe_e0dd;

    /**
     * Map of defined tag.
     */
    public static final Map<Integer, DICOMTag> c = new HashMap<Integer, DICOMTag>() {
        {
            put(FileMetaInformationGroupLength, new DICOMTag(FileMetaInformationGroupLength,
                    "File Meta Information Group Length",
                    DICOMValueRepresentation.c.get("UL")));
            put(FileMetaInformationVersion, new DICOMTag(FileMetaInformationVersion,
                    "File Meta Information Version",
                    DICOMValueRepresentation.c.get("OB")));
            put(MediaStorageSOPClassUID, new DICOMTag(MediaStorageSOPClassUID,
                    "Media Storage SOP Class UID",
                    DICOMValueRepresentation.c.get("UI")));
            put(MediaStorageSOPInstanceUID, new DICOMTag(MediaStorageSOPInstanceUID,
                    "Media Storage SOP Instance UID",
                    DICOMValueRepresentation.c.get("UI")));
            put(TransferSyntaxUID, new DICOMTag(TransferSyntaxUID,
                    "TransferSyntax UID",
                    DICOMValueRepresentation.c.get("UI")));
            put(ImplementationClassUID, new DICOMTag(ImplementationClassUID,
                    "Implementation Class UID",
                    DICOMValueRepresentation.c.get("UI")));
            put(ImplementationVersionName, new DICOMTag(ImplementationVersionName,
                    "Implementation Version Name",
                    DICOMValueRepresentation.c.get("SH")));
            put(SourceApplicationEntity, new DICOMTag(SourceApplicationEntity,
                    "Source Application Entity",
                    DICOMValueRepresentation.c.get("AE")));
            put(PrivateInformationCreatorUID, new DICOMTag(PrivateInformationCreatorUID,
                    "Private Information creator UID",
                    DICOMValueRepresentation.c.get("UI")));
            put(PrivateInformation, new DICOMTag(PrivateInformation,
                    "PrivateInformation",
                    DICOMValueRepresentation.c.get("OB")));
            put(ImageOrientationPatient, new DICOMTag(ImageOrientationPatient,
                    "Image Orientation (Patient)",
                    DICOMValueRepresentation.c.get("DS")));
            put(SamplesPerPixel, new DICOMTag(SamplesPerPixel,
                    "Samples per pixel",
                    DICOMValueRepresentation.c.get("US")));
            put(Rows, new DICOMTag(Rows,
                    "Rows",
                    DICOMValueRepresentation.c.get("US")));
            put(Columns, new DICOMTag(Columns,
                    "Columns",
                    DICOMValueRepresentation.c.get("US")));
            put(PixelSpacing, new DICOMTag(PixelSpacing,
                    "Pixel Spacing",
                    DICOMValueRepresentation.c.get("DS")));
            put(BitsAllocated, new DICOMTag(BitsAllocated,
                    "Bits allocated",
                    DICOMValueRepresentation.c.get("US")));
            put(BitsStored, new DICOMTag(BitsStored,
                    "Bits stored",
                    DICOMValueRepresentation.c.get("US")));
            put(HighBit, new DICOMTag(HighBit,
                    "High Bit",
                    DICOMValueRepresentation.c.get("US")));
            put(PixelRepresentation, new DICOMTag(PixelRepresentation,
                    "Pixel Representation",
                    DICOMValueRepresentation.c.get("US")));
            put(WindowCenter, new DICOMTag(WindowCenter,
                    "Window Center",
                    DICOMValueRepresentation.c.get("DS")));
            put(WindowWidth, new DICOMTag(WindowWidth,
                    "Window Width",
                    DICOMValueRepresentation.c.get("DS")));

            put(PixelData, new DICOMTag(PixelData,
                    "Pixel Data",
                    DICOMValueRepresentation.c.get("UN")));
            put(Item, new DICOMTag(Item,
                    "Item",
                    DICOMValueRepresentation.c.get("UN")));
            put(ItemDelimitationTag, new DICOMTag(ItemDelimitationTag,
                    "Item Delimitation Tag",
                    DICOMValueRepresentation.c.get("UN")));
            put(SequenceDelimitationTag, new DICOMTag(SequenceDelimitationTag,
                    "Sequence Delimitation Tag",
                    DICOMValueRepresentation.c.get("UN")));
            put(SpecificCharacterSet, new DICOMTag(SpecificCharacterSet,
                    "Specific Character Set",
                    DICOMValueRepresentation.c.get("CS")));
            put(ImageType, new DICOMTag(ImageType,
                    "Image Type",
                    DICOMValueRepresentation.c.get("CS")));
            put(Modality, new DICOMTag(Modality,
                    "Modality",
                    DICOMValueRepresentation.c.get("CS")));
            put(CodingSchemeName, new DICOMTag(CodingSchemeName,
                    "Coding Scheme Name",
                    DICOMValueRepresentation.c.get("ST")));
            put(ReferencedSeriesSequence, new DICOMTag(ReferencedSeriesSequence,
                    "Referenced Series Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(ReferencedImageSequence, new DICOMTag(ReferencedImageSequence,
                    "Referenced Image Sequence",
                    DICOMValueRepresentation.c.get("SQ")));

            put(PatientsName, new DICOMTag(PatientsName,
                    "Patient's Name",
                    DICOMValueRepresentation.c.get("PN")));
            put(PatientsBirthDate, new DICOMTag(PatientsBirthDate,
                    "Patient's Birth Date",
                    DICOMValueRepresentation.c.get("DA")));
            put(PatientsAge, new DICOMTag(PatientsAge,
                    "Patient's Age",
                    DICOMValueRepresentation.c.get("AS")));

            // graphic annotation sequence
            put(GraphicAnnotationSequence, new DICOMTag(GraphicAnnotationSequence,
                    "Graphic Annotation Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(GraphicLayer, new DICOMTag(GraphicLayer,
                    "Graphic Layer",
                    DICOMValueRepresentation.c.get("CS")));
            put(GraphicAnnotationUnits, new DICOMTag(GraphicAnnotationUnits,
                    "Graphic Annotation Units",
                    DICOMValueRepresentation.c.get("CS")));
            put(UnformattedTextValue, new DICOMTag(UnformattedTextValue,
                    "Unformatted Text Value",
                    DICOMValueRepresentation.c.get("ST")));
            put(TextObjectSequence, new DICOMTag(TextObjectSequence,
                    "Text Object Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(GraphicObjectSequence, new DICOMTag(GraphicObjectSequence,
                    "Graphic Object Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(AnchorPoint, new DICOMTag(AnchorPoint,
                    "Anchor Point",
                    DICOMValueRepresentation.c.get("FL")));
            put(AnchorPointVisibility, new DICOMTag(AnchorPointVisibility,
                    "Anchor Point Visibility",
                    DICOMValueRepresentation.c.get("CS")));
            put(GraphicDimensions, new DICOMTag(GraphicDimensions,
                    "Graphic Dimensions",
                    DICOMValueRepresentation.c.get("US")));
            put(NumberOfGraphicPoints, new DICOMTag(NumberOfGraphicPoints,
                    "Number of Graphic Points",
                    DICOMValueRepresentation.c.get("US")));
            put(GraphicData, new DICOMTag(GraphicData,
                    "Graphic Data",
                    DICOMValueRepresentation.c.get("FL")));
            put(GraphicType, new DICOMTag(GraphicType,
                    "Graphic Type",
                    DICOMValueRepresentation.c.get("CS")));
            put(GraphicFilled, new DICOMTag(GraphicFilled,
                    "Graphic Filled",
                    DICOMValueRepresentation.c.get("CS")));
            put(GraphicLayerSequence, new DICOMTag(GraphicLayerSequence,
                    "Graphic Layer Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(GraphicLayerOrder, new DICOMTag(GraphicLayerOrder,
                    "Graphic Layer Order",
                    DICOMValueRepresentation.c.get("IS")));
            put(GraphicLayerDescription, new DICOMTag(GraphicLayerDescription,
                    "Graphic Layer Description",
                    DICOMValueRepresentation.c.get("LO")));
            put(ContentDescription, new DICOMTag(ContentDescription,
                    "Content Description",
                    DICOMValueRepresentation.c.get("LO")));
            put(PresentationCreationDate, new DICOMTag(PresentationCreationDate,
                    "Presentation Creation Date",
                    DICOMValueRepresentation.c.get("DA")));
            put(PresentationCreationTime, new DICOMTag(PresentationCreationTime,
                    "Presentation Creation Time",
                    DICOMValueRepresentation.c.get("TM")));
            put(ContentCreatorsName, new DICOMTag(ContentCreatorsName,
                    "Content Creator's Name",
                    DICOMValueRepresentation.c.get("PN")));
            put(LineStyleSequence, new DICOMTag(LineStyleSequence,
                    "Line Style Sequence",
                    DICOMValueRepresentation.c.get("SQ")));
            put(PatternOnColorCIELabValue, new DICOMTag(PatternOnColorCIELabValue,
                    "Pattern On Color CIELab Value",
                    DICOMValueRepresentation.c.get("US")));
            put(LineThickness, new DICOMTag(LineThickness,
                    "Line Thickness",
                    DICOMValueRepresentation.c.get("FL")));
            put(GraphicLayerRecommendedDisplayCIELabValue, new DICOMTag(GraphicLayerRecommendedDisplayCIELabValue,
                    "Graphic Layer Recommended Display CIELab Value",
                    DICOMValueRepresentation.c.get("US")));
        }
    };
    // ---------------------------------------------------------------
    // - VARIABLES
    // ---------------------------------------------------------------
    /**
     * Tag integer value.
     */
    private final int mTag;
    /**
     * Tag description.
     */
    private final String mName;
    /**
     * Tag value representation.
     */
    private final DICOMValueRepresentation mVR;
    // ---------------------------------------------------------------
    // + <static> FUNCTIONS
    // ---------------------------------------------------------------

    /**
     * Create a DICOM tag using a tag integer value.
     *
     * @param tag Tag integer value
     * @return
     */
    public static final DICOMTag createDICOMTag(int tag) {
        // If the tag is known by Droid Dicom Viewer
        if (c.containsKey(tag)) {
            return c.get(tag);
        } else {
            int tagGroup = (tag >> 16) & 0xff;
            // If the tagGroup is an odd Number, the tag is
            // Private
            String name = (tagGroup % 2 == 0) ? "Unknown" : "Private";
            DICOMValueRepresentation VR = DICOMValueRepresentation.c.get("UN");
            return new DICOMTag(tag, name, VR);
        }
    }

    /**
     * Create a DICOM tag using a tag integer value
     * and a value representation.
     *
     * @param tag Tag integer value
     * @param VR  Value representation.
     * @return
     */
    public static final DICOMTag createDICOMTag(int tag, DICOMValueRepresentation VR) {
        String name;
        // If the tag is known by Droid Dicom Viewer
        if (c.containsKey(tag)) {
            // If the VR is the same as a tag in memory, return this tag
            if (VR.getVR().equals(c.get(tag).getValueRepresentation().getVR()))
                return c.get(tag);
            name = c.get(tag).getName();
        } else {
            int tagGroup = (tag >> 16) & 0xff;
            // If the tagGroup is an odd Number, the tag is
            // Private
            name = (tagGroup % 2 == 0) ? "Unknown" : "Private";
        }
        return new DICOMTag(tag, name, VR);
    }
    // ---------------------------------------------------------------
    // + CONSTRUCTOR
    // ---------------------------------------------------------------

    public DICOMTag(int tag, String name, DICOMValueRepresentation VR) {
        mTag = tag;
        mName = name;
        mVR = VR;
    }
    // ---------------------------------------------------------------
    // + FUNCTIONS
    // ---------------------------------------------------------------

    /**
     * @return the mTag
     */
    public int getTag() {
        return mTag;
    }

    /**
     * @return Tag UID as a String (group + element).
     */
    public String toString() {
        return getGroup() + getElement();
    }

    /**
     * @return Tag group as a String.
     */
    public String getGroup() {
        String toReturn = Integer.toHexString((mTag >> 16) & 0xffff);
        while (toReturn.length() < 4)
            toReturn = "0" + toReturn;
        return toReturn;
    }

    /**
     * @return Tag element as a String.
     */
    public String getElement() {
        String toReturn = Integer.toHexString((mTag) & 0xffff);
        while (toReturn.length() < 4)
            toReturn = "0" + toReturn;
        return toReturn;
    }

    /**
     * @return Tag description.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return Value representation.
     */
    public DICOMValueRepresentation getValueRepresentation() {
        return mVR;
    }
}
