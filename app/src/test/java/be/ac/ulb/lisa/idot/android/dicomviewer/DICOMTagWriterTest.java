package be.ac.ulb.lisa.idot.android.dicomviewer;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.Interfaces.ITagWriter;
import be.ac.ulb.lisa.idot.dicom.file.DICOMTagWriter;

/**
 * Created by Iggytoto on 27.10.2016.
 */

public class DICOMTagWriterTest {
    private final String folderPath = "./test/dicms/";
    private final String clean = "test_clean.dcm";
    private final String testInsertNumericTag = "test_ins_num.dcm";
    private final String testReplaceNumericTag = "test_rep_num.dcm";

    @Test
    public void selfTest() throws FileNotFoundException {
        Assert.assertTrue(filesContentAreEqual(folderPath + clean , folderPath + clean));
    }

    @Test
    public void writeTagTest() throws IOException, DICOMException {
        File dir = new File(folderPath);
        if(!dir.exists()){
            Assert.assertTrue(false); // here we go
        }

        ITagWriter tagWriter = null; // init your class here

        File original = new File(folderPath + clean);
        copyFile(original.getAbsolutePath(),original.getAbsolutePath() + "tr1");
        copyFile(original.getAbsolutePath(),original.getAbsolutePath() + "tr2");

        tagWriter = new DICOMTagWriter(folderPath + clean + "tr1");
        ByteBuffer bb1 = ByteBuffer.allocate(4);
        bb1.putInt(0xFFFFFFFF);
        tagWriter.writeTag(DICOMTag.createDICOMTag(0x00101010, DICOMValueRepresentation.c.get("UL")),bb1.array());
        Assert.assertTrue(filesContentAreEqual(folderPath + testInsertNumericTag , folderPath + clean + "tr1"));

        tagWriter = new DICOMTagWriter(folderPath + clean + "tr2");
        ByteBuffer bb2 = ByteBuffer.allocate(2);
        bb2.putShort((short)0xffff);
        tagWriter.writeTag(DICOMTag.createDICOMTag(0x00280002, DICOMValueRepresentation.c.get("US")),bb2.array());
        Assert.assertTrue(filesContentAreEqual(folderPath + testReplaceNumericTag , folderPath + clean + "tr2"));
    }

    private void copyFile(String source, String destination) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    private boolean filesContentAreEqual(String p1,String p2) throws FileNotFoundException {
        byte[] f1Content = readContentIntoByteArray(new File(p1));
        byte[] f2Content = readContentIntoByteArray(new File(p2));

        return Arrays.equals(f1Content,f2Content);
    }

    private byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bFile;
    }
}
