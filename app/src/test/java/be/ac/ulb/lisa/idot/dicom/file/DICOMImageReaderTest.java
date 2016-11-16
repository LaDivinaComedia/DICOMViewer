package be.ac.ulb.lisa.idot.dicom.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import be.ac.ulb.lisa.idot.dicom.data.DICOMBody;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by vlad on 27.10.2016.
 */
public class DICOMImageReaderTest {
    File[] files;

    @Before
    public void setUp() throws Exception {
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

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void hasMetaInformation() throws Exception {
        DICOMImageReader imageReader;
        String fileName;
        for (File file : files) {
            // annotationReader
            if (file.isFile()) {
                fileName = file.getName();
                try {
                    imageReader = new DICOMImageReader(file);
                    if (fileName.endsWith(".dcm")) {
                        assertEquals(true, imageReader.hasMetaInformation());
                    } else {
                        assertEquals(false, imageReader.hasMetaInformation());
                    }
                    imageReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void parse() throws Exception {
        DICOMImageReader imageReader;
        DICOMImage image;
        DICOMBody body;
        DICOMMetaInformation metaInformation;
        LISAImageGray16Bit lisaImage;
        String fileName;
        for (File file : files) {
            if (file.isFile()) {
                fileName = file.getName();
                try {
                    imageReader = new DICOMImageReader(file);
                    image = imageReader.parse();
                    lisaImage = image.getImage();
                    body = image.getBody();
                    metaInformation = image.getMetaInformation();
                    if (fileName.endsWith("chest.dcm")) {
                        assertEquals("19910101", metaInformation.getPatientBirthDate());
                        assertEquals("PORTABLE^CHEST", metaInformation.getPatientName());
                        assertArrayEquals(new float[] { 0.2000f, 0.2000f }, metaInformation.getPixelSpacing(), 0);

                        assertEquals("CR", body.getModality());

                        assertEquals(2140, lisaImage.getHeight());
                        assertEquals(1760, lisaImage.getWidth());
                        assertEquals(511, lisaImage.getWindowCenter());
                        assertEquals(1024, lisaImage.getWindowWidth());
                    } else if (fileName.endsWith("CT.dcm")) {
                        assertEquals("19410101", metaInformation.getPatientBirthDate());
                        assertEquals("Prpqefryjbqj1", metaInformation.getPatientName());
                        assertArrayEquals(new float[] { 0.763672f, 0.763672f }, metaInformation.getPixelSpacing(), 0);

                        assertEquals("CT", body.getModality());

                        assertEquals(512, lisaImage.getHeight());
                        assertEquals(512, lisaImage.getWidth());
                        assertEquals(40, lisaImage.getWindowCenter());
                        assertEquals(400, lisaImage.getWindowWidth());
                    } else if (fileName.endsWith("IM-0001-0004.dcm")) {
                        assertEquals("19490301", metaInformation.getPatientBirthDate());
                        assertEquals("BRAINIX", metaInformation.getPatientName());
                        assertArrayEquals(new float[] { 0.8984375f, 0.8984375f }, metaInformation.getPixelSpacing(), 0);

                        assertEquals("MR", body.getModality());

                        assertEquals(256, lisaImage.getHeight());
                        assertEquals(256, lisaImage.getWidth());
                        assertEquals(224, lisaImage.getWindowCenter());
                        assertEquals(390, lisaImage.getWindowWidth());
                    }
                    imageReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}