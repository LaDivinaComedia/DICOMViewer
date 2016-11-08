package be.ac.ulb.lisa.idot.android.dicomviewer;

import org.junit.Assert;
import org.junit.Test;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.file.DICOMTagComposer;

/**
 * Created by Iggytoto on 09.11.2016.
 */

public class DICOMComposerTest {
    @Test
    public void composerTestWithUL(){
        byte[] expected = new byte[]{
                0x00,0x02,0x00,0x00,  // 0x00020000 = DICOMTag.FileMetaInformationGroupLength
                0x55,0x4c,0x00,0x04,  // 0x554c0004 = UL 4
                0x1b,0x3a,0x0c,0x6d   // 0x1b3a0c6d = 456789101
        };

        byte[] received = DICOMTagComposer.composeTag(DICOMTag.createDICOMTag(DICOMTag.FileMetaInformationGroupLength),
                456789101);

        Assert.assertArrayEquals(expected,received);
    }

    @Test
    public void composerTestWithIS(){
        byte[] expected = new byte[]{
                0x00,0x20,0x00,0x13,  // 0x00200013 = DICOMTag.InstanceNumber
                0x49,0x53,0x00,0x03,  // 0x49530004 = IS 3
                0x31,0x30,0x31        // 0x313031   = "101"
        };

        byte[] received = DICOMTagComposer.composeTag(DICOMTag.createDICOMTag(DICOMTag.InstanceNumber),
                101);

        Assert.assertArrayEquals(expected,received);
    }

    @Test
    public void composerTestWithCS(){
        byte[] expected = new byte[]{
                0x00,0x08,0x00,0x60,  // 0x00080060 = DICOMTag.Modality
                0x43,0x53,0x00,0x17,  // 0x43530017 = CS 23
                0x54,0x68,0x69,0x73,  // "This"
                0x20,0x69,0x73,0x20,  // " is "
                0x74,0x68,0x65,0x20,  // "the "
                0x74,0x65,0x73,0x74,  // "test"
                0x20,0x73,0x74,0x72,  // " str"
                0x69,0x6e,0x67        // "ing"
        };

        byte[] received = DICOMTagComposer.composeTag(DICOMTag.createDICOMTag(DICOMTag.Modality),
                "This is the test string");

        Assert.assertArrayEquals(expected,received);
    }
}
