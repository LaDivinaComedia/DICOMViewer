package be.ac.ulb.lisa.idot.dicom.file;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMBody;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

public class DICOMImageReader extends DICOMReader {

    public DICOMImageReader(File file) throws FileNotFoundException {
        super(file);
    }

    public DICOMImageReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    /**
     * Parse the image DICOM file.
     *
     * @throws IOException
     * @throws EOFException
     * @throws DICOMException
     */
    public final DICOMImage parse() throws IOException, EOFException, DICOMException {
        // Variables declaration
        DICOMMetaInformation metaInformation;
        boolean isExplicit;
        short compressionStatus = DICOMImage.UNKNOWN_STATUS;
        // Parse meta information
        if (hasMetaInformation()) {
            metaInformation = parseMetaInformation();
            String transferSyntaxUID = metaInformation.getTransferSyntaxUID();
            if (transferSyntaxUID.equals("1.2.840.10008.1.2")) {
                isExplicit = false;
                setByteOrder(LITTLE_ENDIAN);
                compressionStatus = DICOMImage.UNCOMPRESSED;
            } else if (transferSyntaxUID.equals("1.2.840.10008.1.2.1")) {
                isExplicit = true;
                setByteOrder(LITTLE_ENDIAN);
                compressionStatus = DICOMImage.UNCOMPRESSED;
            } else if (transferSyntaxUID.equals("1.2.840.10008.1.2.2")) {
                isExplicit = true;
                setByteOrder(BIG_ENDIAN);
                compressionStatus = DICOMImage.UNCOMPRESSED;
            } else {
                isExplicit = true;
                setByteOrder(LITTLE_ENDIAN);
                compressionStatus = DICOMImage.COMPRESSED;
                // Compressed image are not supported yet => throw a exception
                throw new DICOMException("The image is compressed."
                        + " This is not supported yet.");
            }
        } else {
            metaInformation = null;
            isExplicit = false;
            setByteOrder(LITTLE_ENDIAN);
        }
        // Parse the body
        DICOMImageReaderFunctions dicomReaderFunctions =
                new DICOMImageReaderFunctions(isExplicit, compressionStatus, metaInformation);
        parse(null, 0xffffffffL, isExplicit, dicomReaderFunctions, true);
        DICOMImage dicomImage = new DICOMImage(metaInformation,
                dicomReaderFunctions.getBody(),
                dicomReaderFunctions.getImage(),
                compressionStatus);
        return dicomImage;
    }

    protected class DICOMImageReaderFunctions implements DICOMReaderFunctions {
        private DICOMMetaInformation mMetadataInformation;
        // TODO support encapsulated PixelData ? or throw an error

        DICOMBody mBody;
        LISAImageGray16Bit mImage;
        boolean mIsExplicit;
        short mCompressionStatus;

        public DICOMImageReaderFunctions(boolean isExplicit, short compressionStatus) {
            mBody = new DICOMBody();
            mImage = new LISAImageGray16Bit();
            mIsExplicit = isExplicit;
            mCompressionStatus = compressionStatus;
        }

        public DICOMImageReaderFunctions(boolean isExplicit, short compressionStatus, DICOMMetaInformation metaInformation) {
            mBody = new DICOMBody();
            mImage = new LISAImageGray16Bit();
            mIsExplicit = isExplicit;
            mCompressionStatus = compressionStatus;
            mMetadataInformation = metaInformation;
        }

        public void addDICOMElement(DICOMElement parent, DICOMElement element) {
            // If this is a sequence, do nothing
            if (parent != null)
                return;
            int tag = element.getDICOMTag().getTag();
            switch (tag){
                case DICOMTag.SpecificCharacterSet:
                    mBody.setSpecificCharset(element.getValueString());
                    mSpecificCharset = mBody.getSpecificCharset();
                    break;
                case DICOMTag.ImageType:
                    mBody.setImageType(element.getValueString());
                    break;
                case DICOMTag.ImageOrientationPatient:
                    mImage.setImageOrientation(getImageOrientation(element));
                    break;
                case DICOMTag.SamplesPerPixel:
                    mBody.setSamplesPerPixel(element.getValueInt());
                    break;
                case DICOMTag.Rows:
                    mImage.setHeight((short) element.getValueInt());
                    break;
                case DICOMTag.Columns:
                    mImage.setWidth((short) element.getValueInt());
                    break;
                case DICOMTag.BitsAllocated:
                    mBody.setBitsAllocated(element.getValueInt());
                    break;
                case DICOMTag.BitsStored:
                    mBody.setBitsStored(element.getValueInt());
                    mImage.setGrayLevel((int) Math.pow(2, mBody.getBitsStored()));
                    break;
                case DICOMTag.HighBit:
                    mBody.setHightBit(element.getValueInt());
                    break;
                case DICOMTag.PixelRepresentation:
                    mBody.setPixelRepresentation(element.getValueInt());
                    break;
                case DICOMTag.WindowCenter:
                    mImage.setWindowCenter(getIntFromStringArray(element));
                    break;
                case DICOMTag.WindowWidth:
                    mImage.setWindowWidth(getIntFromStringArray(element));
                    break;
                case DICOMTag.PatientsBirthDate:
                    mMetadataInformation.setPatientBirthDate(element.getValueString());
                    break;
                case DICOMTag.PatientsName:
                    mMetadataInformation.setPatientName(element.getValueString());
                    break;
                case DICOMTag.PatientsAge:
                    mMetadataInformation.setPatientAge(element.getValueString());
                    break;
                case DICOMTag.PixelSpacing:
                    String o = (String) element.getValue();
                    double[] ps = new double[2];
                    ps[0] = Double.valueOf(o.substring(0, o.indexOf("\\")));
                    ps[1] = Double.valueOf(o.substring(o.indexOf("\\") + 1));
                    mMetadataInformation.setPixelSpacing(ps);
                    break;
                case DICOMTag.Modality:
                    mMetadataInformation.setModality(element.getValueString());
                    break;
            }
        }

        public boolean isRequiredElement(int tag) {
            return (tag == DICOMTag.SpecificCharacterSet)
                    || (tag == DICOMTag.ImageType)
                    || (tag == DICOMTag.ImageOrientationPatient)
                    || (tag == DICOMTag.SamplesPerPixel)
                    || (tag == DICOMTag.Rows)
                    || (tag == DICOMTag.Columns)
                    || (tag == DICOMTag.BitsAllocated)
                    || (tag == DICOMTag.BitsStored)
                    || (tag == DICOMTag.HighBit)
                    || (tag == DICOMTag.PixelRepresentation)
                    || (tag == DICOMTag.WindowCenter)
                    || (tag == DICOMTag.WindowWidth)
                    || (tag == DICOMTag.PatientsBirthDate)
                    || (tag == DICOMTag.PatientsName)
                    || (tag == DICOMTag.PatientsAge)
                    || (tag == DICOMTag.PixelSpacing)
                    || (tag == DICOMTag.Modality);
        }

        public void computeImage(DICOMElement parent,
                                 DICOMValueRepresentation VR, long valueLength)
                throws IOException, EOFException, DICOMException {
            // If the image is compressed, or if the compression status
            // is unknown or if the parent exists or if the bits
            // allocated is not defined, skip it
            if (mCompressionStatus == DICOMImage.UNKNOWN_STATUS
                    || mCompressionStatus == DICOMImage.COMPRESSED
                    || mBody.getBitsAllocated() == 0
                    || parent != null) {
                if (valueLength == 0xffffffffL) {
                    throw new DICOMException("Cannot skip the PixelData" +
                            " because the length is undefined");
                } else {
                    skip(valueLength);
                    mByteOffset += valueLength;
                    return;
                }
            }
            // Check the samples per pixel, just 1 is implemented yet
            if (mBody.getSamplesPerPixel() != 1)
                throw new DICOMException("The samples per pixel ("
                        + mBody.getSamplesPerPixel() + ") is not"
                        + " supported yet.");
            // For Implicit: OW and little endian
            if (!mIsExplicit) {
                computeOWImage(valueLength);
                // Explicit VR
            } else {
                // If it is OB return because OB is not
                // supported yet
                if (VR.equals("OB")) {
                    skip(valueLength);
                    mByteOffset += valueLength;
                    return;
                    // TODO throw an error if bits allocated > 8
                    // and VR == OB because PS 3.5-2009 Pg. 66-68:
                    // If the bits allocated > 8 => OW !
                    // But it's not done because we do not know
                    // if this specification of the standard is
                    // respected and we do not implement for now
                    // the OB reading.
                } else if (VR.equals("OW")) {
                    computeOWImage(valueLength);
                } else {
                    // Unknown data pixel value representation
                    throw new DICOMException("Unknown PixelData");
                }
            }
        }

        public DICOMBody getBody() {
            return mBody;
        }

        public LISAImageGray16Bit getImage() {
            return mImage;
        }

        /**
         * Compute an
         *
         * @param valueLength
         * @throws IOException
         * @throws EOFException
         * @throws DICOMException
         */
        private void computeOWImage(long valueLength)
                throws IOException, EOFException, DICOMException {
            // Check the value length
            if (valueLength == 0xffffffffL)
                throw new DICOMException("Cannot parse PixelData " +
                        "because the length is undefined");
            // Get the bits allocated
            int bitsAllocated = mBody.getBitsAllocated();
            // Cf. PS 3.5-2009 Pg. 66-67
            if (bitsAllocated == 8) {
                computeOW8BitImage(valueLength);
            } else if (bitsAllocated == 16) {
                computeOW16BitImage(valueLength);
            } else if (bitsAllocated == 32) {

				/* for (int i = 0; i < mPixelData.length; i++) {
                    mPixelData[i] = (int) readUnsignedLong();
				}
			
				mByteOffset += valueLength; */
                // TODO We can sample the gray level on 16 bit but
                // is it compatible with the DICOM standard ?
                throw new DICOMException("This image cannot be parsed "
                        + "because the bits allocated value ("
                        + bitsAllocated
                        + ") is not supported yet.");
            } else if (bitsAllocated == 64) {

				/* for (int i = 0; i < mPixelData.length; i++) {
                    mPixelData[i] = (int) readUnsignedLong64();
				}
				
				mByteOffset += valueLength; */
                // TODO We can sample the gray level on 16 bit but
                // is it compatible with the DICOM standard ?
                throw new DICOMException("This image cannot be parsed "
                        + "because the bits allocated value ("
                        + bitsAllocated
                        + ") is not supported yet.");
            } else {
                throw new DICOMException("This image cannot be parsed "
                        + "because the bits allocated value ("
                        + bitsAllocated
                        + ") is not supported yet.");
            }
            // Add the value length to the byte offset
            mByteOffset += valueLength;
        }

        private void computeOW16BitImage(long valueLength)
                throws IOException, EOFException, DICOMException {
            // Check that the value length correspond to 2 * width * height
            if (valueLength != (2 * mImage.getWidth() * mImage.getHeight()))
                throw new DICOMException("The size of the image does not " +
                        "correspond to the size of the Pixel Data coded " +
                        "in byte.");
            // Get the bit shift (e.g.: highBit = 11, bitsStored = 12
            // => 11 - 12 + 1 = 0 i.e. no bit shift), (e.g.: highBit = 15,
            // bitsStored = 12 => 15 - 12 + 1 = 4
            int bitShift = mBody.getHightBit() - mBody.getBitsStored() + 1;
            int grayLevel = mImage.getGrayLevel();
            int[] imageData = new int[(int) (valueLength / 2)];
            int imageDataMax = 0;
            int[] imageHistogram = new int[grayLevel];
            int imageHistogramMax = 0;
            if (bitShift == 0) {
                for (int i = 0; i < imageData.length; i++) {
                    imageData[i] = readUnsignedInt16() & 0x0000ffff;
                    if (imageData[i] > imageDataMax)
                        imageDataMax = imageData[i];
                    if (imageData[i] >= 0 && imageData[i] < grayLevel) {
                        imageHistogram[imageData[i]] += 1;
                        if (imageHistogram[imageData[i]] > imageHistogramMax)
                            imageHistogramMax = imageHistogram[imageData[i]];
                    }
                }
            } else {
                for (int i = 0; i < imageData.length; i++) {
                    imageData[i] = (readUnsignedInt16() >> bitShift) & 0x0000ffff;
                    if (imageData[i] > imageDataMax)
                        imageDataMax = imageData[i];
                    if (imageData[i] >= 0 && imageData[i] < grayLevel) {
                        imageHistogram[imageData[i]] += 1;
                        if (imageHistogram[imageData[i]] > imageHistogramMax)
                            imageHistogramMax = imageHistogram[imageData[i]];
                    }
                }
            }
            mImage.setData(imageData);
            mImage.setDataMax(imageDataMax);
            mImage.setHistogramData(imageHistogram);
            mImage.setHistogramMax(imageHistogramMax);
        }

        private void computeOW8BitImage(long valueLength)
                throws IOException, EOFException, DICOMException {
            // Check that the value length correspond to 2 * width * height
            if (valueLength != (mImage.getWidth() * mImage.getHeight()))
                throw new DICOMException("The size of the image does not " +
                        "correspond to the size of the Pixel Data coded " +
                        "in byte.");
            // Get the bit shift (e.g.: highBit = 4, bitsStored = 5
            // => 4 - 5 + 1 = 0 i.e. no bit shift), (e.g.: highBit = 6,
            // bitsStored = 5 => 6 - 5 + 1 = 2
            int bitShift = mBody.getHightBit() - mBody.getBitsStored() + 1;
            int grayLevel = mImage.getGrayLevel();
            int[] imageData = new int[(int) (valueLength)];
            int imageDataMax = 0;
            int[] imageHistogram = new int[grayLevel];
            int imageHistogramMax = 0;
            if (bitShift == 0) {
                for (int i = 0; i < imageData.length; i++) {
                    imageData[i] = read() & 0x000000ff;
                    if (imageData[i] > imageDataMax)
                        imageDataMax = imageData[i];
                    if (imageData[i] >= 0 && imageData[i] < grayLevel) {
                        imageHistogram[imageData[i]] += 1;
                        if (imageHistogram[imageData[i]] > imageHistogramMax)
                            imageHistogramMax = imageHistogram[imageData[i]];
                    }
                }
            } else {
                for (int i = 0; i < imageData.length; i++) {
                    imageData[i] = (read() >> bitShift) & 0x000000ff;
                    if (imageData[i] > imageDataMax)
                        imageDataMax = imageData[i];
                    if (imageData[i] >= 0 && imageData[i] < grayLevel) {
                        imageHistogram[imageData[i]] += 1;
                        if (imageHistogram[imageData[i]] > imageHistogramMax)
                            imageHistogramMax = imageHistogram[imageData[i]];
                    }
                }
            }
            mImage.setData(imageData);
            mImage.setDataMax(imageDataMax);
            mImage.setHistogramData(imageHistogram);
            mImage.setHistogramMax(imageHistogramMax);
        }

        private int getIntFromStringArray(DICOMElement element) {
            // Explode the string
            String[] values = element.getValueString().split("\\\\");
            if (values.length >= 1) {
                try {
                    // We do this because if the value is coded as
                    // a float single
                    return Math.round(Float.parseFloat(values[0]));
                } catch (NumberFormatException ex) {
                    return -1;
                }
            }
            return -1;
        }

        private float[] getImageOrientation(DICOMElement element) {
            // Explode the string
            String[] values = element.getValueString().split("\\\\");
            if (values.length != 6)
                return null;
            float[] imageOrientation = new float[6];
            for (int i = 0; i < 6; i++) {
                try {
                    imageOrientation[i] = Float.parseFloat(values[i]);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return imageOrientation;
        }
    }
}
