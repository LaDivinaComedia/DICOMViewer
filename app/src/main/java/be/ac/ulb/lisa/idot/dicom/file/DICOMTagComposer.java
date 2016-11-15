package be.ac.ulb.lisa.idot.dicom.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;

/**
 * Created by Iggytoto on 04.11.2016.
 */

public class DICOMTagComposer {

    public static ByteOrder ByteOrder = java.nio.ByteOrder.LITTLE_ENDIAN;

    public static byte[] composeTag(DICOMTag tag, Object value) {
        switch (tag.getValueRepresentation().getVR()) {
            case "UL":
                return createUnsingedLongTag(tag, value);
            case "OB":
            case "UI":
            case "SH":
            case "CS":
                return createStringTag(tag,value);
            case "IS":
                return createStringTag(tag,Integer.toString((int)value));

            default:
                return new byte[0];
        }
    }

    /**
     * Converts string tag representation
     * @param tag tag number , 0xGGGGXXXX
     * @param value string object expected
     * @return byte array tag representation
     */
    private static byte[] createStringTag(DICOMTag tag, Object value) {
        String valueToPack = (String) value;
        return composeTag(
                tag.getTag(),
                tag.getValueRepresentation().getVR(),
                stringToByteArray(valueToPack));
    }

    /**
     * Converts long or array of long to tag bytes
     * @param tag valid tag
     * @param value 4byte UL or array of 4byte UL's (basically this is ints)
     * @return
     */
    private static byte[] createUnsingedLongTag(DICOMTag tag, Object value) {
        Integer[] valueToPack;
        if(value instanceof Integer){
            valueToPack = new Integer[]{(int)value};
        }
        else if(value instanceof Integer[]){
            valueToPack = (Integer[]) value;
        }
        else{
            return new byte[0]; // todo return error
        }

        return composeTag(
                tag.getTag(),
                tag.getValueRepresentation().getVR(),
                intArrayToByteArray(valueToPack));
    }

    /**
     * Converter integer array to seqence of bytes
     * @param array
     * @return
     */
    private static byte[] intArrayToByteArray(Integer[] array){
        ByteBuffer bb =  ByteBuffer.allocate(array.length*4);
        bb.order(ByteOrder);
        for(int i:array){
            bb.putInt(i);
        }
        return bb.array();
    }

    /**
     * Composes tag from given parameters
     * @param tagName as integer, 0xGGGGXXXX
     * @param vr as value representation string, "XX"
     * @param data  as byte array as data
     * @return tag as byte array , [0xGGGGXXXX][VR][LSH / 00LINT][data], where LSH - length
     * represented in short and LINT - length represented in int
     */
    private static byte[] composeTag(int tagName,String vr,byte[] data){
        short[] tag = splitDicomTag(tagName);
        boolean isLengthOnTwoBytes = DICOMReader.hasValueLengthOn2Bytes(vr);
        ByteBuffer bb = ByteBuffer.allocate(4 + (isLengthOnTwoBytes ? 4 : 8) + data.length);
        bb.order(ByteOrder);
        bb.putShort(tag[0]);            //0xGGGG    -   groupname
        bb.putShort(tag[1]);                    //0xTTTT    -   TAG
        bb.put(stringToByteArray(vr));
        if(isLengthOnTwoBytes){
            bb.putShort((short)data.length);
        }
        else{
            bb.putShort((short) 0);
            bb.putInt(data.length);
        }
        bb.put(data);
        return bb.array();
    }

    /**
     * Converts string to byte representation according to UTF-8
     * @param s string to convert
     * @return byte array length of s, length of s+1 if s.length mod 2 != 0, last byte is 00
     */
    private static byte[] stringToByteArray(String s){
        byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
        if(s.length() % 2 != 0){
            return ByteBuffer.allocate(s.length() + 1).put(bytes).put((byte) 0).array();
        }
        return bytes;
    }

    /**
     * Splits dicom tag presentation from int to short[2]
     * @param tag
     * @return short[2] where [0] is 0xGGGG - group , [1] - 0xTTTT - tag
     */
    public static short[] splitDicomTag(int tag){
        return new short[]{(short)(tag >> 16),(short) tag };
    }
}
