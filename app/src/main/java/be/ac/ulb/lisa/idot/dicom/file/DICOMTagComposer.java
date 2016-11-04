package be.ac.ulb.lisa.idot.dicom.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import be.ac.ulb.lisa.idot.dicom.DICOMTag;

/**
 * Created by Iggytoto on 04.11.2016.
 */

public class DICOMTagComposer {

    public static byte[] composeTag(DICOMTag tag, Object value) {
        switch (tag.getValueRepresentation().getName()) {
            case "UL":
                return createUnsingedLongTag(tag, value);
            default:
                return new byte[0];
        }
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

        return ByteBuffer.allocate(4 + 4 + valueToPack.length*4)
                .putInt(tag.getTag())
                .put("UL".getBytes(Charset.forName("UTF-8")))
                .putShort((short) valueToPack.length)
                .put(intArrayToByteArray(valueToPack))
                .array();
    }

    private static byte[] intArrayToByteArray(Integer[] array){
        ByteBuffer bb =  ByteBuffer.allocate(array.length*4);
        for(int i:array){
            bb.putInt(i);
        }
        return bb.array();
    }
}
