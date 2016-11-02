package be.ac.ulb.lisa.idot.dicom.file;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMItem;
import be.ac.ulb.lisa.idot.dicom.DICOMSequence;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;

/**
 * DICOM Reader.
 *
 * @author Pierre Malarme
 * @author Vladyslav Vasyliev
 * @version 1.2
 */
public class DICOMReader extends DICOMBufferedInputStream {
    // ---------------------------------------------------------------
    // - <static> VARIABLES
    // ---------------------------------------------------------------

    /**
     * Length of the preamble.
     */
    private static final int PREAMBLE_LENGTH = 128;

    /**
     * Prefix of DICOM file.
     */
    private static final String PREFIX = "DICM";
    private static final byte PREFIX_D = 68;
    private static final byte PREFIX_I = 73;
    private static final byte PREFIX_C = 67;
    private static final byte PREFIX_M = 77;

    // mModality of the image. Used in order to distinct presentation state files
    private String mModality = "";
    protected long mByteOffset = 0;
    protected String mSpecificCharset = "ASCII";
    protected long mFileSize = 0;

    public DICOMReader(File file) throws FileNotFoundException {
        super(file);
        mFileSize = file.length();
        mark(Integer.MAX_VALUE);
    }

    public DICOMReader(String fileName) throws FileNotFoundException {
        super(fileName);
        File file = new File(fileName);
        mFileSize = file.length();
        mark(Integer.MAX_VALUE);
    }

    /**
     * Read floating point value.
     *
     * @param valueLength
     * @return
     * @throws IOException
     */
    protected Object readFL(long valueLength) throws IOException {
        // if the value length is greater than 4 it is an array of float
        Object result = null;
        if (valueLength == 4) {
            result = readFloatSingle();
            mByteOffset += 4;
        } else {
            int size = (int) (valueLength / 4);
            float[] results = new float[size];
            for (int i = 0; i < size; i++) {
                results[i] = readFloatSingle();
                mByteOffset += 4;
            }
            result = results;
        }
        return result;
    }

    /**
     * Read double precision floating point value.
     *
     * @param valueLength
     * @return
     * @throws IOException
     */
    protected Object readFD(long valueLength) throws IOException {
        // if the value length is greater than 8 it is an array of double
        Object result = null;
        if (valueLength == 8) {
            result = readFloatDouble();
            mByteOffset += 8;
        } else {
            int size = (int) (valueLength / 8);
            double[] results = new double[size];
            for (int i = 0; i < size; i++) {
                results[i] = readFloatDouble();
                mByteOffset += 8;
            }
            result = results;
        }
        return result;
    }

    /**
     * Read signed binary integer 32 bits long in 2's complement form.
     *
     * @param valueLength
     * @return Signed binary integer 32 bits long in 2's complement form.
     * @throws IOException
     */
    protected Object readSL(long valueLength) throws IOException {
        Object result = null;
        // if the value length is greater than 4 it is an array of int
        if (valueLength == 4) {
            result = readSignedLong();
            mByteOffset += 4;
        } else {
            int size = (int) (valueLength / 4);
            int[] results = new int[size];
            for (int i = 0; i < size; i++) {
                results[i] = readSignedLong();
                mByteOffset += 4;
            }
            result = results;
        }
        return result;
    }

    /**
     * Read signed binary integer 16 bits long in 2's complement form.
     *
     * @param valueLength
     * @return Signed binary integer 16 bits long in 2's complement form.
     * @throws IOException
     */
    protected Object readSS(long valueLength) throws IOException {
        Object result = null;
        // if the value length is greater than 2 it is an array of short
        if (valueLength == 2) {
            result = readSignedShort();
            mByteOffset += 2;
        } else {
            int size = (int) (valueLength / 2);
            short[] results = new short[size];
            for (int i = 0; i < size; i++) {
                results[i] = readSignedShort();
                mByteOffset += 2;
            }
            result = results;
        }
        return result;
    }

    /**
     * Read unsigned binary integer 16 bits long.
     *
     * @param valueLength
     * @return Unsigned binary integer 16 bits long.
     * @throws IOException
     */
    protected Object readUS(long valueLength) throws IOException {
        Object result = null;
        // if the value length is greater than 2 it is an array of int
        if (valueLength == 2) {
            result = readUnsignedShort();
            mByteOffset += 2;
        } else {
            int size = (int) (valueLength / 2);
            int[] results = new int[size];
            for (int i = 0; i < size; i++) {
                results[i] = readUnsignedShort();
                mByteOffset += 2;
            }
            result = results;
        }
        return result;
    }

    /**
     * @return True if the file is a DICOM file and has meta information
     * false otherwise.
     * @throws IOException
     */
    public final boolean hasMetaInformation() throws IOException {
        // Reset the BufferedInputStream
        if (mByteOffset > 0) {
            reset();
            mark(Integer.MAX_VALUE);
        }
        // If the file is smaller than the preamble and prefix
        // length there is no meta information
        if (available() < (PREAMBLE_LENGTH + PREFIX.length()))
            return false;
        // Skip the preamble
        skip(PREAMBLE_LENGTH);
        // Get the prefix
        String prefix = readASCII(PREFIX.length());
        // Check the prefix
        boolean toReturn = prefix.equals(PREFIX);
        // Reset the BufferedInputStream
        reset();
        mark(Integer.MAX_VALUE);
        // Skip the byte offset (mByteOffset)
        if (mByteOffset > 0)
            skip(mByteOffset);
        return toReturn;
    }

    /**
     * Parse meta information.
     *
     * @throws IOException
     * @throws EOFException
     * @throws DICOMException
     */
    public final DICOMMetaInformation parseMetaInformation()
            throws IOException, DICOMException {
        // Reset the BufferedInputStream
        if (mByteOffset > 0) {
            reset();
            mark(Integer.MAX_VALUE);
            mByteOffset = 0;
        }
        try {
            // Skip the preamble
            skip(PREAMBLE_LENGTH);
            mByteOffset += PREAMBLE_LENGTH;
            // Check the prefix
            byte[] ASCIIbyte = new byte[PREFIX.length()];
            read(ASCIIbyte);
            if (ASCIIbyte[0] != PREFIX_D
                    || ASCIIbyte[1] != PREFIX_I
                    || ASCIIbyte[2] != PREFIX_C
                    || ASCIIbyte[3] != PREFIX_M)
                throw new DICOMException("This is not a DICOM file");
            mByteOffset += 4;
            // Create a DICOM meta information object
            DICOMMetaInformation metaInformation = new DICOMMetaInformation();
            // Tag File Meta group length = the length of
            // the meta of the dicom file
            int tag = readTag();
            mByteOffset += 4;
            // If this is not this tag => error because the
            // DICOM 7.1 (3.5-2009) tags must be ordered by increasing
            // data element
            if (tag != DICOMTag.FileMetaInformationGroupLength)
                throw new DICOMException("Meta Information has now length");
            // Skip 4 byte because we now that it is an UL
            skip(4);
            mByteOffset += 4;
            // Get the FileMeta group length
            long groupLength = readUnsignedLong(); // this is magic shit dunno ...
            mByteOffset += 4;
            // Set the group length (meta information length)
            metaInformation.setGroupLength(groupLength);
            DICOMMetaInformationReaderFunctions dicomReaderFunctions =
                    new DICOMMetaInformationReaderFunctions(metaInformation);
            // Fast parsing of the header with escape sequences
            parse(null, groupLength, true, dicomReaderFunctions, true);
            // Return the meta information
            return metaInformation;
        } catch (EOFException ex) {
            throw new EOFException(
                    "Cannot read the Meta Information of the DICOM file\n\n"
                            + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException(
                    "Cannot read the Meta Information of the DICOM file\n\n"
                            + ex.getMessage());
        }
    }

    /**
     * Parse the DICOM file.
     *
     * @param parentElement        If a sequence is parsed.
     * @param length               The length to parse. 0xffffffffL is
     *                             the undefined length.
     * @param isExplicit           Set if the content of the BufferedInputStream
     *                             has explicit (true) or implicit (false) value representation.
     * @param dicomReaderFunctions Implementation of the DICOMReaderFunctions
     *                             interface.
     * @param skipSequence         Set if the sequence must be skipped (true)
     *                             or not (false).
     * @throws IOException
     * @throws EOFException   If the end of the file is reached before
     *                        the end of the parsing.
     * @throws DICOMException
     */
    protected void parse(DICOMItem parentElement, long length, boolean isExplicit,
                         DICOMReaderFunctions dicomReaderFunctions, boolean skipSequence)
            throws IOException, EOFException, DICOMException {
        // Set if the the length of the element to parse is defined
        boolean isLengthUndefined = length == 0xffffffffL;
        try {
            // Variable declaration and initialization
            DICOMTag dicomTag = null;
            DICOMValueRepresentation vr = null;
            long valueLength = 0;
            int tag = 0;
            length = length & 0xffffffffL;
            long lastByteOffset = isLengthUndefined ? 0xffffffffL : mByteOffset + length - 1;
            // Loop while the length is undefined or while the byte offset is smaller than
            // the last byte offset
            while (((isLengthUndefined) || (mByteOffset < lastByteOffset))
                    && (mByteOffset < mFileSize)) {
                DICOMElement element = null;
                // Read the tag
                tag = readTag();
                mByteOffset += 4;
                if (tag == 0) {
                    reset();
                    mark(Integer.MAX_VALUE);
                    skip(mByteOffset - 4);
                    tag = readTag();
                }
                // If the tag is an item delimitation skip 4 bytes because there are
                // 4 null bytes after the item delimitation tag
                if (tag == DICOMTag.ItemDelimitationTag) {
                    skip(4);
                    mByteOffset += 4;
                    return;
                }
                // If the tag is an Item, ignore it and skip 4 bytes because there are
                // 4 null bytes after the item delimitation tag
                if (tag == DICOMTag.Item) {
                    skip(4);
                    mByteOffset += 4;
                    continue;
                }
                // Get the value representation and length and create the DICOMTag.
                if (isExplicit) {
                    // Get the DICOM value representation code/abreviation
                    String VRAbbreviation = readASCII(2);
                    mByteOffset += 2;
                    vr = DICOMValueRepresentation.c.get(VRAbbreviation);
                    vr = (vr == null) ? DICOMValueRepresentation.c.get("UN") : vr;
                    dicomTag = DICOMTag.createDICOMTag(tag, vr);
                    // If the value is on 2 bytes
                    if (hasValueLengthOn2Bytes(vr.getVR())) {
                        valueLength = readUnsignedInt16();
                        mByteOffset += 2;
                    } else {
                        skip(2); // Because VR abbreviation is coded on 2 bytes
                        valueLength = readUnsignedLong();
                        mByteOffset += 6; // 2 for the skip and 4 for the unsigned long
                    }
                } else {
                    dicomTag = DICOMTag.createDICOMTag(tag);
                    vr = dicomTag.getValueRepresentation();
                    vr = (vr == null) ? DICOMValueRepresentation.c.get("UN") : vr;
                    // If the value lengths are implicit, the length of the value
                    // comes directly after the tag
                    valueLength = readUnsignedLong();
                    mByteOffset += 4;
                }
                valueLength = valueLength & 0xffffffffL;
                // Get the value. If it is a sequence, read a new sequence
                if (vr.equals("SQ") || vr.equals("UN") && valueLength == 0xffffffffL) {
                    // If the attribute has undefined value length and/or do not skip sequence
                    if (!skipSequence || valueLength == 0xffffffffL) {
                        // Parse the sequence
                        element = new DICOMSequence(dicomTag);
                        if ("PR".equals(mModality))
                            // notify the reader function about the entry into the sequence
                            dicomReaderFunctions.addDICOMElement(parentElement, element);
                        parseSequence((DICOMSequence) element, valueLength, isExplicit,
                                dicomReaderFunctions, skipSequence);
                        if ("PR".equals(mModality))
                            element.setDICOMTag(DICOMTag.createDICOMTag(DICOMTag.SequenceDelimitationTag));
                    } else {
                        // Skip the value length
                        skip(valueLength);
                        mByteOffset += valueLength;
                        continue;
                    }
                    // Else if tag is PixelData
                } else if (tag == DICOMTag.PixelData) {
                    dicomReaderFunctions.computeImage(parentElement, vr, valueLength);
                    continue; // Return to the while begin
                } else if (valueLength != 0xffffffffL) {
                    // If it's not a required element, skip it
                    if ((parentElement != null || !dicomReaderFunctions.isRequiredElement(tag))
                            && !"PR".equals(mModality)) {
                        skip(valueLength);
                        mByteOffset += valueLength;
                        continue;
                    }
                    Object value = null;
                    if (vr.equals("UL")) {
                        if (valueLength == 4) {
                            value = readUnsignedLong();
                            mByteOffset += 4;
                        } else {
                            int size = (int) (valueLength / 4);
                            long[] values = new long[size];
                            for (int i = 0; i < size; i++) {
                                values[i] = readUnsignedLong();
                                mByteOffset += 4;
                            }
                            value = values;
                        }
                    } else if (vr.equals("AT")) {
                        value = readTag();
                        mByteOffset += 4;
                    } else if (vr.equals("OB") || vr.equals("OF")
                            || vr.equals("OW")) {
                        String valueString = "";
                        for (int i = 0; i < valueLength; i++) {
                            valueString += (i == 0) ? "" : "\\";
                            valueString += String.valueOf(read());
                            mByteOffset++;
                        }
                        value = valueString;
                    } else if (vr.equals("FL")) {
                        value = readFL(valueLength);
                    } else if (vr.equals("FD")) {
                        value = readFD(valueLength);
                    } else if (vr.equals("SL")) {
                        value = readSL(valueLength);
                    } else if (vr.equals("SS")) {
                        value = readSS(valueLength);
                    } else if (vr.equals("US")) {
                        value = readUS(valueLength);
                    } else if (vr.equals("LO") || vr.equals("LT")
                            || vr.equals("PN") || vr.equals("SH")
                            || vr.equals("ST") || vr.equals("UT")) {
                        value = readString((int) valueLength, mSpecificCharset);
                        mByteOffset += valueLength;
                        // else interpreted as ASCII String
                    } else {
                        value = readASCII((int) valueLength);
                        if (tag == DICOMTag.Modality)
                            mModality = (String) value;
                        mByteOffset += valueLength;
                    }
                    // Create the element
                    element = new DICOMElement(dicomTag, valueLength, value);
                }
                if (element != null) {
                    // Add the DICOM element
                    dicomReaderFunctions.addDICOMElement(parentElement, element);
                }
            } // end of the while
            // End of the stream exception
        } catch (EOFException e) {
            if (!isLengthUndefined)
                throw new EOFException();
            // I/O Exception
        } catch (IOException e) {
            if (!isLengthUndefined)
                throw new IOException();
        }
    }

    /**
     * Parse a DICOM sequence.
     *
     * @param sequence             DICOM sequence to parse.
     * @param length               Length of DICOM sequence.
     * @param isExplicit           Set if the content of the BufferedInputStream
     *                             has explicit (true) or implicit (false) value representation.
     * @param dicomReaderFunctions Implementation of the DICOMReaderFunctions
     *                             interface.
     * @param skipSequence         Set if the sequence must be skipped (true)
     *                             or not (false).
     * @throws IOException
     * @throws DICOMException
     * @throws EOFException   If the end of the file is reached before
     *                        the end of the parsing.
     */
    protected void parseSequence(DICOMSequence sequence, long length, boolean isExplicit,
                                 DICOMReaderFunctions dicomReaderFunctions, boolean skipSequence)
            throws IOException, DICOMException {
        if (sequence == null) {
            throw new NullPointerException("Null Sequence");
        }
        length = length & 0xffffffffL;
        boolean isLengthUndefined = length == 0xffffffffL;
        try {
            long lastByteOffset = isLengthUndefined ? 0xffffffffL
                    : mByteOffset + length - 1;
            // Loop on all the items
            while (isLengthUndefined || mByteOffset < lastByteOffset) {
                // Get the tag
                int tag = readTag();
                mByteOffset += 4;
                long valueLength = readUnsignedLong();
                mByteOffset += 4;
                // If the tag is an Item
                if (tag == DICOMTag.SequenceDelimitationTag) {
                    break;
                } else if (tag == DICOMTag.Item) {
                    DICOMItem item = new DICOMItem();
                    parse(item, valueLength, isExplicit,
                            dicomReaderFunctions, skipSequence);
                    sequence.addChild(item);
                    // else if the tag is different that end of sequence, this is not a sequence item
                } else {
                    throw new DICOMException("Error Sequence: unknown tag" + (tag >> 16) + (tag & 0xffff));
                }
            }
        } catch (EOFException e) {
            if (!isLengthUndefined)
                throw new EOFException();
        } catch (IOException ex) {
            if (!isLengthUndefined)
                throw new IOException(ex.getMessage());
        }
    }

    /**
     * Check if the value representation is on 2 bytes.
     *
     * @param VR DICOM value representation code on 2 bytes (character).
     * @return
     */
    protected static final boolean hasValueLengthOn2Bytes(String VR) {
        return VR.equals("AR") || VR.equals("AE") || VR.equals("AS") || VR.equals("AT")
                || VR.equals("CS") || VR.equals("DA") || VR.equals("DS") || VR.equals("DT")
                || VR.equals("FD") || VR.equals("FL") || VR.equals("IS") || VR.equals("LO")
                || VR.equals("LT") || VR.equals("PN") || VR.equals("SH") || VR.equals("SL")
                || VR.equals("SL") || VR.equals("SS") || VR.equals("ST") || VR.equals("TM")
                || VR.equals("UI") || VR.equals("UL") || VR.equals("US");
    }
    // ---------------------------------------------------------------
    // # CLASS
    // ---------------------------------------------------------------

    /**
     * Implementation of the DICOMReaderFunctions for
     * meta information.
     *
     * @author Pierre Malarme
     * @version 1.O
     */
    protected class DICOMMetaInformationReaderFunctions implements DICOMReaderFunctions {

        private DICOMMetaInformation mMetaInformation;

        public DICOMMetaInformationReaderFunctions() {
            mMetaInformation = new DICOMMetaInformation();
        }

        public DICOMMetaInformationReaderFunctions(DICOMMetaInformation metaInformation) {
            mMetaInformation = metaInformation;
        }

        public void addDICOMElement(DICOMElement parent, DICOMElement element) {
            // If this is a sequence, do nothing
            if (parent != null)
                return;
            int tag = element.getDICOMTag().getTag();
            switch (tag) {
                case DICOMTag.MediaStorageSOPClassUID:
                    mMetaInformation.setSOPClassUID(element.getValueString());
                    break;
                case DICOMTag.MediaStorageSOPInstanceUID:
                    mMetaInformation.setSOPInstanceUID(element.getValueString());
                    break;
                case DICOMTag.TransferSyntaxUID:
                    mMetaInformation.setTransferSyntaxUID(element.getValueString());
                    break;
                case DICOMTag.ImplementationClassUID:
                    mMetaInformation.setImplementationClassUID(element.getValueString());
                    break;
                case DICOMTag.ImplementationVersionName:
                    mMetaInformation.setImplementationVersionName(element.getValueString());
                    break;
                case DICOMTag.SourceApplicationEntity:
                    mMetaInformation.setAET(element.getValueString());
                    break;
            }
        }

        public boolean isRequiredElement(int tag) {
            return (tag == DICOMTag.MediaStorageSOPClassUID)
                    || (tag == DICOMTag.MediaStorageSOPInstanceUID)
                    || (tag == DICOMTag.TransferSyntaxUID)
                    || (tag == DICOMTag.ImplementationClassUID)
                    || (tag == DICOMTag.ImplementationVersionName)
                    || (tag == DICOMTag.SourceApplicationEntity);
        }

        public void computeImage(DICOMElement parent, DICOMValueRepresentation VR,
                                 long length) throws IOException, EOFException, DICOMException {
            throw new IOException("PixelData in Meta Information.");
        }
    }

}
