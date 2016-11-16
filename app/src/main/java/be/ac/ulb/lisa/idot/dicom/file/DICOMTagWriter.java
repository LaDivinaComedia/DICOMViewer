package be.ac.ulb.lisa.idot.dicom.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;


/**
 * Created by Iggytoto on 18.10.2016.
 */

public class DICOMTagWriter extends DICOMReader {

    private final String mFileName;

    public DICOMTagWriter(String filename) throws FileNotFoundException {
        super(filename);
        this.mFileName = filename;
    }


    /**
     * Writes tag to DICOM image. Does not have tag-to-valuetype type-check.
     *
     * @param tag tag specification to write
     * @param v byte[] value representation
     * @throws IOException
     * @throws DICOMException
     */
    public void writeTag(int tag, byte[] v) throws IOException, DICOMException {

        long tagOffset = searchTag(tag);
        boolean replace = true;
        if (tagOffset < 0) {
            tagOffset *= -1;
            replace = false;
        }
        resetReader();
        byte[] beforeTag; // preparing bytes that were before tag
        byte[] afterTag; //preparing bytes that will be after tag

        //compute before tag part
        beforeTag = new byte[(int) tagOffset];
        read(beforeTag);

        if (replace) {
            //compute tag part
            long oldTagLength = skipTag(); // we skip old tag

            //compute after tag part
            int afterTagLength = (int) (mFileSize - beforeTag.length - oldTagLength);
            afterTag = new byte[afterTagLength];
            read(afterTag);
        } else {
            //compute after tag part
            int afterTagLength = (int) (mFileSize - beforeTag.length);
            afterTag = new byte[afterTagLength];
            read(afterTag);
        }
        close();

        File f = new File(mFileName);
        if(f.exists()){
            f.delete();
        }

        FileOutputStream fs = new FileOutputStream(mFileName);
        fs.write(beforeTag);
        fs.write(v);
        fs.write(afterTag);
        fs.close();
    }

    /**
     * Skips next tag, returns bytes skipped
     */
    private long skipTag() throws IOException {
        int tag = readTag();
        long skipped = 0;
        mByteOffset += 4;
        if (tag == 0) {
            reset();
            mark(Integer.MAX_VALUE);
            skip(mByteOffset - 4);
            tag = readTag();
        }
        skipped += 4;
        DICOMValueRepresentation vr = readValueRepresentation();
        skipped += 2;
        skipped += hasValueLengthOn2Bytes(vr.getVR()) ? 2 : 6;
        long vl = readValueLength(tag, vr);
        skip(vl);
        skipped += vl;
        mByteOffset += vl;
        return skipped;
    }

    /**
     * Search offset of given tag
     *
     * @param tag
     * @return offset in bytes, its negative if tag does not exists but place found
     * @throws IOException
     * @throws DICOMException
     */
    private long searchTag(int tag) throws IOException, DICOMException {

        int prevTag = 0;
        int currentTag = 0;

        // Reset the BufferedInputStream
        if (mByteOffset > 0) {
            resetReader();
        }

        readPreamble();
        // Tag File Meta group length = the length of
        // the meta of the dicom file
        currentTag = readTag();
        mByteOffset += 4;
        // If this is not this tag => error because the
        // DICOM 7.1 (3.5-2009) tags must be ordered by increasing
        // data element
        if (currentTag != 0x00020000)
            throw new DICOMException("Meta Information has no length");
        // Skip 4 byte because we now that it is an UL
        skip(4);
        mByteOffset += 4;
        // Get the FileMeta group length
        readUnsignedLong();
        mByteOffset += 4;

        while (mByteOffset < mFileSize) {
            // Read the tag
            prevTag = currentTag;
            currentTag = readTag();
            mByteOffset += 4;

            if (currentTag == 0) {
                reset();
                mark(Integer.MAX_VALUE);
                skip(mByteOffset - 4);
                currentTag = readTag();
            }

            if (currentTag == tag) {
                return mByteOffset - 4;
            } else if (prevTag < tag && currentTag > tag) {
                return -(mByteOffset - 4);
            }

            DICOMValueRepresentation vr = readValueRepresentation();
            long vl = readValueLength(currentTag, vr);

            skip(vl);
            mByteOffset += vl;
        }
        throw new DICOMException("Error searching spot for tag.");
    }

    /**
     * Reads preamble and DICM prefix and ensures that is DICOM file
     *
     * @throws IOException
     * @throws DICOMException if something wrong with DICM preamble or missing
     */
    private void readPreamble() throws IOException, DICOMException {
        // Skip the preamble
        skip(PREAMBLE_LENGTH);
        mByteOffset += PREAMBLE_LENGTH;

        // Check the prefix
        byte[] ASCIIbyte = new byte[PREFIX.length()];
        read(ASCIIbyte);
        // To avoid the null char : ASCII(0)
        String toReturnString = new String(ASCIIbyte, "ASCII");
        for (int i = 0; i < PREFIX.length(); i++)
            if (ASCIIbyte[i] == 0x00)
                throw new DICOMException("This is not a DICOM file");
        if (!PREFIX.equals(toReturnString))
            throw new DICOMException("This is not a DICOM file");
        if (!(ASCIIbyte[0] == PREFIX_D &&
                ASCIIbyte[1] == PREFIX_I &&
                ASCIIbyte[2] == PREFIX_C &&
                ASCIIbyte[3] == PREFIX_M))
            throw new DICOMException("This is not a DICOM file");
        mByteOffset += 4;
    }

    private void resetReader() throws IOException {
        reset();
        mark(Integer.MAX_VALUE);
        mByteOffset = 0;
    }

    private DICOMValueRepresentation readValueRepresentation() throws IOException {
        // Get the DICOM value representation code/abreviation
        String VRAbbreviation = readASCII(2);
        mByteOffset += 2;
        DICOMValueRepresentation VR = DICOMValueRepresentation.c.get(VRAbbreviation);
        return (VR == null) ? DICOMValueRepresentation.c.get("UN") : VR;
    }

    private long readValueLength(int tag, DICOMValueRepresentation vr) throws IOException {
        long vl = 0;
        // If the value is on 2 bytes
        if (hasValueLengthOn2Bytes(vr.getVR())) {

            vl = readUnsignedInt16();
            mByteOffset += 2;

        } else {

            skip(2); // Because VR abbreviation is coded
            // on 2 bytes

            vl = readUnsignedLong();
            mByteOffset += 6; // 2 for the skip and 4 for the unsigned long
        }

        return vl;
    }

}
