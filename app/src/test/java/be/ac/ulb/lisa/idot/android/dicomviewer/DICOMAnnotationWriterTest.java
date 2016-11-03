package be.ac.ulb.lisa.idot.android.dicomviewer;

import org.junit.Assert;
import org.junit.Test;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.file.DICOMAnnotationWriter;

/**
 * Created by Iggytoto on 03.11.2016.
 */

public class DICOMAnnotationWriterTest extends DICOMAnnotationWriter {

    @Test
    public void createIntArrayTagTest() {
        int[] values = new int[]{31337, 1337};
        int tagValue = 12345678;
        byte[] desired = new byte[]{
                0x00, (byte) 0xBC, 0x61, 0x4e,  // 00bc 614e = 12345678
                0x00, 0x08, 0x00, 0x00,         // 0008 0000
                0x00, 0x00, 0x7a, 0x69,         // 0000 7a69 = 31337
                0x00, 0x00, 0x05, 0x39          // 0000 0539 = 1337
        };
        byte[] result = createIntArrayTag(tagValue, values);
        Assert.assertArrayEquals(desired, result);
    }

    @Test
    public void createIntTagTest() {
        int value = 1488;
        int tagValue = 228;
        byte[] desired = new byte[]{
                0x00, 0x00, 0x00, (byte) 0xe4,         // 0000 00e4 = 228
                0x00, 0x04, 0x00, 0x00,               // 0004 0000
                0x00, 0x00, 0x05, (byte) 0xd0,         // 0000 05d0 = 1488
        };
        byte[] result = createIntTag(tagValue, value);
        Assert.assertArrayEquals(desired, result);
    }

    @Test
    public void createStringTagTest() {
        String value = "Hello!This is Yucko the clown!";
        int tagValue = DICOMTag.HighBit;
        byte[] desired = new byte[]{
                0x00, 0x28, 0x01, 0x02,  // 0028 0102
                0x00, 0x1e, 0x00, 0x00,         // 001e 0000
                0x48, 0x65, 0x6c, 0x6c,         // Hell
                0x6f, 0x21, 0x54, 0x68,         // o!Th
                0x69, 0x73, 0x20, 0x69,         // is i
                0x73, 0x20, 0x59, 0x75,         // s Yu
                0x63, 0x6b, 0x6f, 0x20,         // cko
                0x74, 0x68, 0x65, 0x20,         // the
                0x63, 0x6c, 0x6f, 0x77,         // clow
                0x6e, 0x21                      // n!
        };
        byte[] result = createStringTag(tagValue, value);
        Assert.assertArrayEquals(desired, result);
    }

    @Test
    public void createEmptySeqHeaderTest(){
        int tagValue = DICOMTag.Item;
        byte[] desired = new byte[]{
                (byte)0xff, (byte)0xfe, (byte)0xe0, 0x00,         // 0xfffee000
                0x00, 0x00, 0x00, 0x00,               // 0000 0000
        };
        byte[] result = createSequenceHeader(tagValue, true);
        Assert.assertArrayEquals(desired, result);
    }

    @Test
    public void createSeqHeaderTest(){
        int tagValue = DICOMTag.PatternOnColorCIELabValue;
        byte[] desired = new byte[]{
                0x00, 0x70, 0x02, 0x51,         // 0x0070_0251
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,   // ffff ffff
        };
        byte[] result = createSequenceHeader(tagValue, false);
        Assert.assertArrayEquals(desired, result);

    }

    @Test
    public void createShortTagTest(){
        short value = 6346;
        int tagValue = DICOMTag.PresentationCreationTime;
        byte[] desired = new byte[]{
                0x00, 0x70, 0x00, (byte) 0x83,         // 0x0070_0083
                0x00, 0x02, 0x00, 0x00,
                0x18, (byte)0xca,               // 18ca = 6346
        };
        byte[] result = createShortTag(tagValue, value);
        Assert.assertArrayEquals(desired, result);
    }
}
