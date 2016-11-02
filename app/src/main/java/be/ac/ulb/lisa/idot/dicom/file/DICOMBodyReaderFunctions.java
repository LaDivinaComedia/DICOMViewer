package be.ac.ulb.lisa.idot.dicom.file;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMBody;

import java.io.EOFException;
import java.io.IOException;


/**
 * @author Vladyslav Vasyliev
 * @version 1.0
 */
public class DICOMBodyReaderFunctions implements DICOMReaderFunctions {
    protected DICOMBody mBody;

    public DICOMBodyReaderFunctions() {
        mBody = new DICOMBody();
    }

    @Override
    public void addDICOMElement(DICOMElement parent, DICOMElement element) {
        int tag = element.getDICOMTag().getTag();
        switch (tag) {
            case DICOMTag.SpecificCharacterSet:
                mBody.setSpecificCharset(element.getValueString());
                break;
            case DICOMTag.ImageType:
                mBody.setImageType(element.getValueString());
                break;
            case DICOMTag.SOPClassUID:
                mBody.setSOPClassUID(element.getValueString());
                break;
            case DICOMTag.SOPInstanceUID:
                mBody.setSOPInstanceUID(element.getValueString());
                break;
            case DICOMTag.StudyDate:
                mBody.setStudyDate(element.getValueString());
                break;
            case DICOMTag.SeriesDate:
                break;
            case DICOMTag.StudyTime:
                mBody.setStudyTime(element.getValueString());
                break;
            case DICOMTag.ContentTime:
                break;
            case DICOMTag.AccessionNumber:
                break;
            case DICOMTag.Modality:
                mBody.setModality(element.getValueString());
                break;
            case DICOMTag.ReferringPhysiciansName:
                mBody.setReferringPhysicianName(element.getValueString());
                break;
            case DICOMTag.StationName:
                mBody.setStationName(element.getValueString());
                break;
            case DICOMTag.StudyDescription:
                mBody.setStudyDescription(element.getValueString());
                break;
            case DICOMTag.SeriesDescription:
                break;
            case DICOMTag.CodingSchemeName:
                break;
            case DICOMTag.ReferencedSeriesSequence:
                break;
            case DICOMTag.ReferencedImageSequence:
                break;

            case DICOMTag.SamplesPerPixel:
                mBody.setSamplesPerPixel(element.getValueInt());
                break;
            case DICOMTag.BitsAllocated:
                mBody.setBitsAllocated(element.getValueInt());
                break;
            case DICOMTag.BitsStored:
                mBody.setBitsStored(element.getValueInt());
                break;
            case DICOMTag.HighBit:
                mBody.setHightBit(element.getValueInt());
                break;
            case DICOMTag.PixelRepresentation:
                mBody.setPixelRepresentation(element.getValueInt());
                break;
        }
    }

    @Override
    public boolean isRequiredElement(int tag) {
        return tag == DICOMTag.SpecificCharacterSet
                || tag == DICOMTag.ImageType
                || tag == DICOMTag.SOPClassUID
                || tag == DICOMTag.SOPInstanceUID
                || tag == DICOMTag.StudyDate
                || tag == DICOMTag.SeriesDate
                || tag == DICOMTag.StudyTime
                || tag == DICOMTag.ContentTime
                || tag == DICOMTag.AccessionNumber
                || tag == DICOMTag.Modality
                || tag == DICOMTag.ReferringPhysiciansName
                || tag == DICOMTag.StationName
                || tag == DICOMTag.StudyDescription
                || tag == DICOMTag.SeriesDescription
                || tag == DICOMTag.CodingSchemeName
                || tag == DICOMTag.ReferencedSeriesSequence
                || tag == DICOMTag.ReferencedImageSequence
                || tag == DICOMTag.SamplesPerPixel
                || tag == DICOMTag.BitsAllocated
                || tag == DICOMTag.BitsStored
                || tag == DICOMTag.HighBit
                || tag == DICOMTag.PixelRepresentation;
    }

    @Override
    public void computeImage(DICOMElement parent, DICOMValueRepresentation VR, long valueLength)
            throws IOException, EOFException, DICOMException {
        throw new IOException("DCIOMBodyReaderFunctions.computeImage is not implemented.");
    }

    public DICOMBody getBody() {
        return mBody;
    }

}
