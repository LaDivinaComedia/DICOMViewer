package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.graphics.PointF;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * @author Vladyslav Vasyliev
 *         Created on 13.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class CalculusTest {

    @Test
    public void getRealDistance() throws Exception {
        PointF[] points = new PointF[]{
                new PointF(600, 10),
                new PointF(300, 200)
        };
        float res;
        res = Calculus.getRealDistance(points, 1, 1, 1);
        // ((600 - 300)^2 + (200 - 10)^2)^0.5
        assertEquals(Math.pow(Math.pow(300, 2) + Math.pow(190, 2), 0.5), res, 0.0001);
        // (((600 - 300)*0.7)^2 + ((200 - 10)0.33)^2)^0.5
        res = Calculus.getRealDistance(points, 0.7f, 0.33f, 1);
        assertEquals(Math.pow(Math.pow(300 * 0.7, 2) + Math.pow(190 * 0.33, 2), 0.5), res, 0.0001);
    }

    @Test
    public void getRealSquare() throws Exception {
        // 15 x 10
        int w = 15, h = 10;
        float res;
        float scale = 0.7f;
        float sx = 0.9f, sy = 0.9f;
        res = Calculus.getRealSquare(w * h, scale, sx, sy);
        assertEquals((w * sx / scale) * (h * sy / scale), res, 0.0001);

        sx = 0.5f;
        sy = 1.7f;
        res = Calculus.getRealSquare(w * h, scale, sx, sy);
        assertEquals((w * sx / scale) * (h * sy / scale), res, 0.0001);
    }

    @Test
    public void getRealAngle() throws Exception {
        PointF[] points = new PointF[]{
                new PointF(0, 0),
                new PointF(100, 100),
                new PointF(0, 100)
        };
        float sx = 0.5f, sy = 0.5f;
        float res;
        res = Calculus.getRealAngle(points, sx, sy);
        assertEquals(45, res, 0.0001);
        // strange thing, it should be otherwise...
        sy = 1.0f;
        sx = 0.75f;
        res = Calculus.getRealAngle(points, sx, sy);
        assertEquals(53.13, res, 0.005);
    }

}