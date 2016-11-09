package be.ac.ulb.lisa.idot.dicom.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMGraphicObject;
import be.ac.ulb.lisa.idot.dicom.data.DICOMPresentationState;
import be.ac.ulb.lisa.idot.dicom.data.DICOMTextObject;

import static be.ac.ulb.lisa.idot.dicom.DICOMTag.GraphicLayer;

/**
 * Created by Iggytoto on 02.11.2016.
 */

public class DICOMAnnotationWriter {
    private final byte[] mUndefinedLength = new byte[]{
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};    //0xffffffff
    private final byte[] mItemTag = new byte[]{
            (byte) 0xff, (byte) 0xfe, (byte) 0xe0, 0x00,            //0xfffee000
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};    //0xffffffff
    private final byte[] mSequenceDelimiter = new byte[]{
            (byte) 0xff, (byte) 0xfe, (byte) 0xe0, (byte) 0xdd,     //0xfffee0dd
            0x00, 0x00, 0x00, 0x00};                                //0x00000000
    private final byte[] mItemDelimiter = new byte[]{
            (byte) 0xff, (byte) 0xfe, (byte) 0xe0, 0x0d,            //0xfffee00d
            0x00, 0x00, 0x00, 0x00};                                //0x00000000
    private final byte[] mEmptyLength = new byte[]{0, 0, 0, 0};     //0x00000000

    public void writeAnnotations(DICOMPresentationState state,String filename) throws IOException {
        byte[] preamble = getPreamble();
        byte[] body = convertAnnotations(state);

        byte[] bytesToWrite = ByteBuffer.allocate(preamble.length + body.length)
                .put(preamble)
                .put(body)
                .order(ByteOrder.LITTLE_ENDIAN)
                .array();

        FileOutputStream f = new FileOutputStream(filename);
        f.write(bytesToWrite);
        f.close();
    }

    public byte[] convertAnnotations(DICOMPresentationState state) {

        //header
        byte[] hdr = createHeader(state);
        //00700001
        byte[] gas = createGraphicAnnotationSequence(state.getAnnotations());
        //00700060
        byte[] gls = createGraphicLayerSequence(state.getAnnotations());
        //footer
        byte[] ftr = createFooter(state.getMetaInformation().getContentDescription(),
                state.getMetaInformation().getContentLabel(),
                state.getMetaInformation().getContentCreatorsName());

        return ByteBuffer.allocate(hdr.length + gas.length + gls.length + ftr.length)
                .put(hdr)
                .put(gas)
                .put(gls)
                .put(ftr)
                .array();
    }

    private byte[] createHeader(DICOMPresentationState ps) {
        byte[] t00020001 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.FileMetaInformationVersion),
                ps.getMetaInformation().getFileMetaInformationVersion());
        byte[] t00020002 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.MediaStorageSOPClassUID),
                ps.getMetaInformation().getSOPClassUID());
        byte[] t00020003 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.SOPInstanceUID),
                ps.getMetaInformation().getSOPInstanceUID());
        byte[] t00020010 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.TransferSyntaxUID),
                ps.getMetaInformation().getTransferSyntaxUID());
        byte[] t00020012 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.ImplementationClassUID),
                ps.getMetaInformation().getImplementationClassUID());
        byte[] t00020013 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.ImplementationVersionName),
                ps.getMetaInformation().getImplementationVersionName());
        int sectionLength =
                t00020001.length +
                t00020002.length +
                t00020003.length +
                t00020010.length +
                t00020012.length +
                t00020013.length;
        byte[] t00020000 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.FileMetaInformationGroupLength),
                sectionLength);
        byte[] t00080060 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.SpecificCharacterSet),
                ps.getBody().getSpecificCharset());
        byte[] t00200013 = DICOMTagComposer.composeTag(DICOMTag.c.get(DICOMTag.InstanceNumber),
                ps.getBody().getInstanceNumber());
        return ByteBuffer.allocate(
                t00020000.length +
                sectionLength +
                t00080060.length +
                t00200013.length)
                .put(t00020000)
                .put(t00020001)
                .put(t00020002)
                .put(t00020003)
                .put(t00020010)
                .put(t00020012)
                .put(t00020013)
                .put(t00080060)
                .put(t00200013)
                .array();
    }

    public byte[] createFooter(String label, String desc, String name) {
        Date d = new Date();
        String date = String.format("%s%s%s", d.getYear(), d.getMonth(), d.getDay());
        String time = String.format("%s%s", d.getHours(), d.getMinutes());
        return ByteBuffer.allocate(8 + label.length() + 8 + desc.length() + 8 + date.length() + 8 + time.length() + 8 + name.length())
                .put(createStringTag(DICOMTag.ContentLabel, label)) // 8 + label.len
                .put(createStringTag(DICOMTag.ContentDescription, desc)) // 8 + desc.len
                .put(createStringTag(DICOMTag.PresentationCreationDate, date)) // 8+date.len
                .put(createStringTag(DICOMTag.PresentationCreationDate, time)) // 8 + time.len
                .put(createStringTag(DICOMTag.ContentCreatorsName, name)) // 8 + name.len
                .array();
    }

    protected byte[] createGraphicLayerSequence(List<DICOMAnnotation> annotations) {
        Map<String, Integer> layerMap = new HashMap<>();

        byte[] sequenceHeader = createSequenceHeader(DICOMTag.GraphicLayerSequence, false);
        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;
        for (DICOMAnnotation a : annotations) {
            if (!layerMap.containsKey(a.getLayerName())) {
                layerMap.put(a.getLayerName(), a.getLayerOrder());
            }
        }

        for (String s : layerMap.keySet()) {
            byte[] item = ByteBuffer.allocate(8 + 8 + s.length() + 8 + 2 + 8)
                    .put(mItemTag)//8
                    .put(createStringTag(DICOMTag.GraphicLayer, s)) // 8 + str.len
                    .put(createStringTag(DICOMTag.GraphicLayerOrder, layerMap.get(s).toString() + " "))//8+2
                    .put(mItemDelimiter)//8
                    .array();
            arraysLength += item.length;
        }

        ByteBuffer bb = ByteBuffer.allocate(sequenceHeader.length + arraysLength + mSequenceDelimiter.length)
                .put(sequenceHeader);
        for (byte[] a : arrays) {
            bb.put(a);
        }
        bb.put(mSequenceDelimiter);
        return bb.array();
    }

    protected byte[] createGraphicAnnotationSequence(List<DICOMAnnotation> annotations) {
        byte[] sequenceHeader = createSequenceHeader(DICOMTag.GraphicAnnotationSequence, false);
        //item

        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;
        for (DICOMAnnotation a : annotations) {
            byte[] ann = annotationToByteArray(a);
            arrays.add(mItemTag);
            arrays.add(ann);
            arrays.add(mItemDelimiter);
            arraysLength += ann.length + mItemDelimiter.length + mItemTag.length;
        }

        ByteBuffer bb = ByteBuffer.allocate(sequenceHeader.length + arraysLength + mSequenceDelimiter.length)
                .put(sequenceHeader);
        for (byte[] a : arrays) {
            bb.put(a);
        }
        bb.put(mSequenceDelimiter);
        return bb.array();
    }

    protected byte[] annotationToByteArray(DICOMAnnotation a) {
        // 0x00700002 Graphic Layer DRAW
        byte[] grLayer = createStringTag(GraphicLayer, "DRAW");
        // 0x00700008 Texts objects sequence
        byte[] texts = createTextObjects(a.getTextObjects());
        byte[] graphics = createGraphicObjects(a.getGraphicObjects());

        return ByteBuffer.allocate(grLayer.length + texts.length + graphics.length + mSequenceDelimiter.length)
                .put(grLayer)
                .put(texts)
                .put(graphics)
                .put(mSequenceDelimiter)
                .array();
    }

    protected byte[] createGraphicObjects(List<DICOMGraphicObject> graphicObjects) {
        //sequence header
        byte[] tagHeader = createSequenceHeader(DICOMTag.GraphicObjectSequence, graphicObjects.isEmpty());
        //return like 7000 0900 0000 0000 if it has no elements
        if (graphicObjects.size() == 0) {
            return ByteBuffer.allocate(8)
                    .put(tagHeader)
                    .putInt(0)
                    .putInt(0)
                    .array();
        }

        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;

        //pack all text elements
        for (DICOMGraphicObject obj : graphicObjects) {
            int[] points = new int[obj.getNumberOfGraphicPoints() * 2];
            for (int i = 0; i < obj.getPoints().size(); i++) {
                points[i] = Float.floatToIntBits(obj.getPoints().get(i).x);
                points[i + 1] = Float.floatToIntBits(obj.getPoints().get(i).y);
            }

            byte[] objectBytes = ByteBuffer.allocate(8 + 6 + 8 + 2 + 8 + 2 + 8 + points.length + 8 + obj.getGraphicType().length() + 8 + 2 + 8)
                    .put(createStringTag(DICOMTag.GraphicAnnotationUnits, "PIXEL ")) // 8 + 6
                    .put(createShortTag(DICOMTag.GraphicDimensions, (short) 2)) // 8 + 2
                    .put(createShortTag(DICOMTag.NumberOfGraphicPoints, (short) obj.getNumberOfGraphicPoints()))//8+2
                    .put(createIntArrayTag(DICOMTag.GraphicData, points)) //8 + points.len
                    .put(createStringTag(DICOMTag.GraphicType, obj.getGraphicType()))//8 + str.len
                    .put(createStringTag(DICOMTag.GraphicFilled, obj.isGraphicFilled() ? "Y " : "N ")) // 8 + str.len
                    .put(mItemDelimiter) // 8
                    .array();
            arrays.add(objectBytes);
            arraysLength += objectBytes.length;
        }

        ByteBuffer result = ByteBuffer.allocate(tagHeader.length + arraysLength + mSequenceDelimiter.length);
        // tag them
        result.put(tagHeader);
        //put em down
        for (byte[] arr : arrays) {
            result.put(arr);
        }
        //delimit em all
        result.put(mSequenceDelimiter);
        return result.array();
    }

    protected byte[] createShortTag(int number, short item) {
        return ByteBuffer.allocate(10)
                .putInt(number)
                .putShort((short) 2)
                .putShort((short) 0)
                .putShort(item)
                .array();
    }

    protected byte[] createTextObjects(List<DICOMTextObject> textObjects) {
        //sequence header
        byte[] tagHeader = createSequenceHeader(DICOMTag.TextObjectSequence, textObjects.isEmpty());
        ;
        //return like 7000 0800 0000 0000 if it has no elements
        if (textObjects.size() == 0) {
            return ByteBuffer.allocate(8)
                    .put(tagHeader)
                    .putInt(0)
                    .putInt(0)
                    .array();
        }

        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;
        //pack all text elements
        for (DICOMTextObject obj : textObjects) {
            byte[] objectBytes = ByteBuffer.allocate(8 + 6 + 8 + obj.getText().length() + 4 + 4 + 4 + 8 + 2 + 8)
                    .put(createStringTag(DICOMTag.GraphicAnnotationUnits, "PIXEL ")) // 8 + 6
                    .put(createStringTag(DICOMTag.UnformattedTextValue, obj.getText())) // 8 + str.len
                    .put(createIntArrayTag(DICOMTag.GraphicObjectSequence, new int[]{Float.floatToIntBits(obj.getTextAnchor().x),
                            Float.floatToIntBits(obj.getTextAnchor().y)})) // 4 + 4 + 4
                    .put(createStringTag(DICOMTag.AnchorPointVisibility, obj.isAnchorVisible() ? "Y " : "N ")) //8 + 2
                    //TODO add 0x0070,0232 Line Style Sequence
                    .put(mItemDelimiter) // 8
                    .array();
            arrays.add(objectBytes);
            arraysLength += objectBytes.length;
        }

        ByteBuffer result = ByteBuffer.allocate(tagHeader.length + arraysLength + mSequenceDelimiter.length);
        // tag them
        result.put(tagHeader);
        //put em down
        for (byte[] arr : arrays) {
            result.put(arr);
        }
        //delimit em all
        result.put(mSequenceDelimiter);
        return result.array();
    }

    protected byte[] createSequenceHeader(int tagName, boolean isEmpty) {
        //sequence tag data, implicit undefined data SQ,
        return ByteBuffer.allocate(8)
                .putInt(tagName)        // tag
                .put(isEmpty ? mEmptyLength : mUndefinedLength)  // ffff ffff
                .array();
    }

    protected byte[] createStringTag(int number, String str) {
        ByteBuffer bb = ByteBuffer.allocate(8 + str.length());
        bb.putInt(number);
        bb.putShort((short) str.length());
        bb.putShort((short) 0); //todo fix this;
        bb.put(str.getBytes(Charset.forName("UTF-8")));
        return bb.array();
    }

    protected byte[] createIntTag(int number, int item) {
        return ByteBuffer.allocate(12)
                .putInt(number)
                .putShort((short) 4)
                .putShort((short) 0)
                .putInt(item)
                .array();
    }

    protected byte[] createIntArrayTag(int number, int[] array) {
        ByteBuffer bb = ByteBuffer.allocate(4 + 4 + array.length * 4);
        bb.putInt(number);
        bb.putShort((short) (array.length * 4));
        bb.putShort((short) 0);
        for (int i = 0; i < array.length; i++) {
            bb.putInt(array[i]);
        }
        return bb.array();
    }

    /**
     * GETS DICM PREAMBLE
     * @return
     */
    public byte[] getPreamble() {
        return ByteBuffer.allocate(128 + 4)
                .putInt(128,0x4449434d)
                .array();
    }
}
