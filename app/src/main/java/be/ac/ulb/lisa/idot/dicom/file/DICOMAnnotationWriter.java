package be.ac.ulb.lisa.idot.dicom.file;

import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMGraphicObject;
import be.ac.ulb.lisa.idot.dicom.data.DICOMTextObject;

/**
 * Created by Iggytoto on 02.11.2016.
 */

public class DICOMAnnotationWriter {
    private final byte[] mUndefinedLength = new byte[]{-1,-1,-1,-1};        //0xffffffff
    private final byte[] mItemTag = new byte[] {-1,-2,-32,0};               //0xfffee000
    private final byte[] mSequenceDelimiter = new byte[] {-1,-2,-32,-34};   //0xfffee0dd
    private final byte[] mItemDelimiter = new byte[] {-1,-2,-32,15};        //0xfffee00d
    private ByteOrder mByteOrder = ByteOrder.LITTLE_ENDIAN;
    private final byte[] mEmptyLength = new byte[] {0,0,0,0};               //0x00000000

    public byte[] convertAnnotations(List<DICOMAnnotation> annotations){

        //00700001
        byte[] tag = createSequenceHeader(DICOMTag.GraphicAnnotationSequence,false);
        //item
        byte[] item = createSequenceItemTag();
        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;
        for(DICOMAnnotation a:annotations){
            byte[] ann = annotationToByteArray(a);
            byte[] del = createItemDelimiter();
            arrays.add(ann);
            arrays.add(del);
            arraysLength+= ann.length + del.length;
        }

        byte[] seqDel = createSequenceDelimiter();

        ByteBuffer bb =  ByteBuffer.allocate(tag.length + item.length + arraysLength + seqDel.length)
                .put(tag)
                .put(item);
        for (byte[] a:arrays){
            bb.put(a);
        }
        bb.put(seqDel);
        return bb.array();

        //TODO 00700060

    }

    private byte[] annotationToByteArray(DICOMAnnotation a){
        // 0x00700002 Graphic Layer DRAW
        byte[] grLayer = createStringTag(DICOMTag.GraphicLayer,"DRAW");
        // 0x00700008 Texts objects sequence
        byte[] texts = createTextObjects(a.getTextObjects());
        byte[] graphics = createGraphicObjects(a.getGraphicObjects());
        byte[] seqDelimiter = createSequenceDelimiter();

        return ByteBuffer.allocate(grLayer.length + texts.length + graphics.length + seqDelimiter.length)
                .put(grLayer)
                .put(texts)
                .put(graphics)
                .put(seqDelimiter)
                .array();
    }

    private byte[] createGraphicObjects(List<DICOMGraphicObject> graphicObjects) {
        //sequence header
        byte[] tagHeader = createSequenceHeader(DICOMTag.GraphicObjectSequence,graphicObjects.isEmpty());
        //return like 7000 0900 0000 0000 if it has no elements
        if(graphicObjects.size() == 0){
            return ByteBuffer.allocate(8)
                    .put(tagHeader)
                    .putInt(0)
                    .putInt(0)
                    .array();
        }

        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;

        //pack all text elements
        for (DICOMGraphicObject obj: graphicObjects) {
            int[] points = new int[obj.getNumberOfGraphicPoints()*2];
            for(int i=0;i<obj.getPoints().size();i++){
                points[i] = Float.floatToIntBits(obj.getPoints().get(i).x);
                points[i+1] = Float.floatToIntBits(obj.getPoints().get(i).y);
            }

            byte[] objectBytes = ByteBuffer.allocate(8+6+8+2+8+2+8+points.length+8+obj.getGraphicType().length()+8+2+8)
                    .put(createStringTag(DICOMTag.GraphicAnnotationUnits,"PIXEL ")) // 8 + 6
                    .put(createShortTag(DICOMTag.GraphicDimensions, (short) 2)) // 8 + 2
                    .put(createShortTag(DICOMTag.NumberOfGraphicPoints, (short) obj.getNumberOfGraphicPoints()))//8+2
                    .put(createIntArrayTag(DICOMTag.GraphicData,points)) //8 + points.len
                    .put(createStringTag(DICOMTag.GraphicType,obj.getGraphicType()))//8 + str.len
                    .put(createStringTag(DICOMTag.GraphicFilled,obj.isGraphicFilled() ? "Y ": "N ")) // 8 + str.len
                    .put(createItemDelimiter()) // 8
                    .array();
            arrays.add(objectBytes);
            arraysLength += objectBytes.length;
        }
        //seq delimiter
        byte[] seqDelimiter = createSequenceDelimiter();
        ByteBuffer result = ByteBuffer.allocate(tagHeader.length + arraysLength + seqDelimiter.length);
        // tag them
        result.put(tagHeader);
        //put em down
        for (byte[] arr: arrays){
            result.put(arr);
        }
        //delimit em all
        result.put(seqDelimiter);
        return result.array();
    }

    private byte[] createShortTag(int number, short item) {
        return ByteBuffer.allocate(10)
                .putInt(number)
                .putShort((short) 2)
                .putShort((short) 0)
                .putInt(item)
                .array();
    }

    private byte[] createTextObjects(List<DICOMTextObject> textObjects) {
        //sequence header
        byte[] tagHeader = createSequenceHeader(DICOMTag.TextObjectSequence,textObjects.isEmpty());;
        //return like 7000 0800 0000 0000 if it has no elements
        if(textObjects.size() == 0){
            return ByteBuffer.allocate(8)
                    .put(tagHeader)
                    .putInt(0)
                    .putInt(0)
                    .array();
        }

        ArrayList<byte[]> arrays = new ArrayList<>();
        int arraysLength = 0;
        //pack all text elements
        for (DICOMTextObject obj: textObjects) {
            byte[] objectBytes = ByteBuffer.allocate(8 + 6 + 8 + obj.getText().length() + 4 + 4 + 4 + 8 + 2 + 8)
                    .put(createStringTag(DICOMTag.GraphicAnnotationUnits,"PIXEL ")) // 8 + 6
                    .put(createStringTag(DICOMTag.UnformattedTextValue,obj.getText())) // 8 + str.len
                    .put(createIntArrayTag(DICOMTag.GraphicObjectSequence,new int[]{Float.floatToIntBits(obj.getTextAnchor().x),
                            Float.floatToIntBits(obj.getTextAnchor().y)})) // 4 + 4 + 4
                    .put(createStringTag(DICOMTag.AnchorPointVisibility,obj.isAnchorVisible()? "Y ": "N ")) //8 + 2
                    //TODO add 0x0070,0232 Line Style Sequence
                    .put(createItemDelimiter()) // 8
                    .array();
            arrays.add(objectBytes);
            arraysLength += objectBytes.length;
        }
        //seq delimiter
        byte[] seqDelimiter = createSequenceDelimiter();
        ByteBuffer result = ByteBuffer.allocate(tagHeader.length + arraysLength + seqDelimiter.length);
        // tag them
        result.put(tagHeader);
        //put em down
        for (byte[] arr: arrays){
            result.put(arr);
        }
        //delimit em all
        result.put(seqDelimiter);
        return result.array();
    }

    private byte[] createSequenceHeader(int tagName,boolean isEmpty){
        //sequence tag data, implicit undefined data SQ,
        return ByteBuffer.allocate(8)
                .putInt(tagName)        // tag
                .put(isEmpty ? mEmptyLength : mUndefinedLength)  // ffff ffff
                .array();
    }

    private byte[] createSequenceItemTag(){
        return ByteBuffer.allocate(8)
                .put(mItemTag)          // fffe e000
                .put(mUndefinedLength)  // ffff ffff
                .array();
    }

    private byte[] createSequenceDelimiter(){
        return ByteBuffer.allocate(8)
                .put(mSequenceDelimiter)
                .putLong(0)
                .array();
    }

    private byte[] createItemDelimiter(){
        return ByteBuffer.allocate(8)
                .put(mItemDelimiter)
                .putLong(0)
                .array();
    }

    private byte[] createStringTag(int number, String str){
        ByteBuffer bb = ByteBuffer.allocate(8 + str.length());
        bb.putInt(number);
        bb.putShort((short)str.length());
        bb.putShort((short)0); //todo fix this;
        bb.put(str.getBytes(Charset.forName("UTF-8")));
        return bb.array();
    }

    private byte[] createIntTag(int number,int item){
        return ByteBuffer.allocate(12)
                .putInt(number)
                .putInt(4)
                .putInt(item)
                .array();
    }

    private byte[] createIntArrayTag(int number,int[] array){
        ByteBuffer bb = ByteBuffer.allocate(4 + 4 + array.length * 4);
        bb.putInt(number);
        bb.putShort((short) array.length);
        bb.putShort((short) 0);
        for (int i = 0; i< array.length ;i++){
            bb.putInt(array[i]);
        }
        return bb.array();
    }


}
