package be.ac.ulb.lisa.idot.dicom.file;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Vladyslav Vasyliev
 */
public class DICOMReaderTest {
    private static int SHORT_BYTES = 2;
    private static int INTEGER_BYTES = 4;
    private static int FLOAT_BYTES = 4;
    private static int DOUBLE_BYTES = 8;

    private DICOMReader reader;
    private float[] fl;
    private double[] fd;
    private short[] ss;
    private int[] sl, us;
    private File[] files;

    @Before
    public void setUp() throws Exception {
        fl = new float[]{1, 15, 3.56f, 8.4f};
        fd = new double[]{12, 415, 73.56, 658.4};
        ss = new short[]{5, 4, 3, 8};
        sl = new int[]{5, 4, 3, 8};
        us = new int[]{0, 5, 0, 4};
        String path = "d:/Документы/Иннополис/Methods Deciding what to design/DICOM/";
        files = new File[]{
                new File(path + "chest.dcm"),
                new File(path + "CT.dcm"),
                new File(path + "IM-0001-0001.dcm"),
                new File(path + "IM-0001-0004.dcm"),
                new File(path + "mammo.dcm"),
                new File(path + "skull.dcm")
        };
    }

    @Test
    public void readFL() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.setByteOrder(DICOMBufferedInputStream.BIG_ENDIAN);
        reader.reset();
        assertEquals(fl[0], reader.readFL(FLOAT_BYTES));
        reader.reset();
        assertArrayEquals(fl, (float[]) reader.readFL(FLOAT_BYTES * fl.length), 1E-20f);
    }

    @Test
    public void readFD() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.setByteOrder(DICOMBufferedInputStream.BIG_ENDIAN);
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length);
        assertEquals(fd[0], reader.readFD(DOUBLE_BYTES));
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length);
        assertArrayEquals(fd, (double[]) reader.readFD(DOUBLE_BYTES * fd.length), 1E-20);
    }

    @Test
    public void readSL() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.setByteOrder(DICOMBufferedInputStream.BIG_ENDIAN);
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length + SHORT_BYTES * ss.length);
        assertEquals(sl[0], reader.readSL(INTEGER_BYTES));
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length + SHORT_BYTES * ss.length);
        assertArrayEquals(sl, (int[]) reader.readSL(INTEGER_BYTES * sl.length));
    }

    @Test
    public void readSS() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.setByteOrder(DICOMBufferedInputStream.BIG_ENDIAN);
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length);
        assertEquals(ss[0], reader.readSS(SHORT_BYTES));
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length);
        assertArrayEquals(ss, (short[]) reader.readSS(SHORT_BYTES * ss.length));
    }

    @Test
    public void readUS() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.setByteOrder(DICOMBufferedInputStream.BIG_ENDIAN);
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length + SHORT_BYTES * ss.length);
        assertEquals(us[0], reader.readUS(SHORT_BYTES));
        reader.reset();
        reader.skip(FLOAT_BYTES * fl.length + DOUBLE_BYTES * fd.length + SHORT_BYTES * ss.length);
        assertArrayEquals(us, (int[]) reader.readUS(SHORT_BYTES * us.length));
    }

    @Test
    public void readTags() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.reset();
        reader.setByteOrder(DICOMBufferedInputStream.LITTLE_ENDIAN);
        assertEquals(0x803F_0000, reader.readTag());
        assertEquals(0x7041_0000, reader.readTag());
        assertEquals(0x6340_0AD7, reader.readTag());
        assertEquals(0x0641_6666, reader.readTag());
    }

    @Test
    public void readAscii() throws Exception {
        reader = new DICOMReader("d:/Projects/Idea/DicomIO/test.data");
        reader.reset();
        assertEquals("?", reader.readASCII(1));
    }

    @Test
    public void parseAnnotations() throws Exception {
    }

    @Test
    public void parse() throws Exception {
    }

    @Test
    public void parseSequence() throws Exception {
    }

    @Test
    public void hasValueLengthOn2Bytes() throws Exception {

    }
}