package be.ac.ulb.lisa.idot.dicom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Vladyslav Vasyliev
 *         Created on 16.11.16.
 */

public class CloneFactory {
    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            baos.close();
            bais.close();
            oos.close();
            ois.close();
            return obj;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
