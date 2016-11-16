package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMGraphicObject;
import be.ac.ulb.lisa.idot.dicom.data.DICOMPresentationState;
import be.ac.ulb.lisa.idot.dicom.data.DICOMTextObject;


/**
 * @author vvanichkov 24.10.2016.
 */
public class AnnotationView extends ToolView implements View.OnTouchListener {
    private static final float RADIUS = 5;
    private static final double SENSITIVITY = 70;
    //    private ArrayList<Paint> mPaints = new ArrayList<Paint>();
    private ArrayList<CustomPath> mCustomPaths = new ArrayList<CustomPath>();
    private ArrayList<Paint> mPaints;
    private ArrayList<Paint> mPaintsText;
    private CustomPath mCurrentPath;
    private float mScaleFactor;
    private int mImageWidth;
    private int mImageHeight;
    private boolean inbound = true;
    private PointF startPoint = null;
    private boolean lateStart = true;
    private int mIndexToDelete;
    private GestureDetector mGestureDetector = new GestureDetector(this.getContext(), new GestureListener());
    private boolean mDoubleTap = false;
    private DICOMPresentationState mPresentationState;
    private float[] mLeftCorner;
    private PointF pointToSaveTextAnnotation;


    public AnnotationView(Context context) {
        super(context);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
    }

    @Override
    protected void init() {
        mCurrentPath = new CustomPath();
        if (mPaints == null)
            mPaints = new ArrayList<Paint>();
        mPaints.add(initPaint());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = this.getWidth() / 2;
        float centerY = this.getHeight() / 2;
        if ((centerX - this.mImageWidth * this.mScaleFactor / 2) > x || (centerX + this.mImageWidth * this.mScaleFactor / 2) < x
                ||
                (centerY - this.mImageHeight * this.mScaleFactor / 2) > y || (centerY + this.mImageHeight * this.mScaleFactor / 2) < y
                ) {
            if ((centerX - this.mImageWidth * this.mScaleFactor / 2) > x)
                x = centerX - this.mImageWidth * this.mScaleFactor / 2;
            else if ((centerX + this.mImageWidth * this.mScaleFactor / 2) < x)
                x = (centerX + this.mImageWidth * this.mScaleFactor / 2);
            if ((centerY - this.mImageHeight * this.mScaleFactor / 2) > y)
                y = (centerY - this.mImageHeight * this.mScaleFactor / 2);
            else if ((centerY + this.mImageHeight * this.mScaleFactor / 2) < y)
                y = (centerY + this.mImageHeight * this.mScaleFactor / 2);
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (this.startPoint == null) {
                    startPoint = new PointF(event.getX(), event.getY());
                }
                if (!lateStart) {
                    touch_start(x, y);
                    this.invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!inbound) {
                    inbound = true;
                    //init();
                    touch_start(x, y);
                }
                if (lateStart) {
                    touch_start(x, y);
                    lateStart = false;
                }
                touch_move(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                double distance = Math.sqrt(Math.pow(startPoint.x - x, 2) + Math.sqrt(Math.pow(startPoint.y - y, 2)));
                if (distance > 10 && mCurrentPath.paths.size() > 5) {
                    touch_up();
                    mCurrentPath.mPath.lineTo(mCurrentPath.mStart.x, mCurrentPath.mStart.y);

                    this.mCustomPaths.add(this.mCurrentPath);
                    DICOMAnnotation diann = mPresentationState.getAnnotations().get(0);
                    DICOMGraphicObject diobj = new DICOMGraphicObject();
                    diobj.setGraphicType(DICOMGraphicObject.GraphicTypes.POLYLINE);
                    diobj.setNumberOfGraphicPoints(mCurrentPath.paths.size());
                    diobj.setPoints(changingPoints(mCurrentPath.paths));
                    diann.getGraphicObjects().add(diobj);
                    askForAnnotationText(true);
                    this.invalidate();
                    init();
                }
                startPoint = null;
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private List<PointF> changingPoints(List<PointF> points) {
        ArrayList<PointF> pointFs = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            pointFs.add(new PointF((points.get(i).x - mLeftCorner[0]) / mScaleFactor, (points.get(i).y - mLeftCorner[1]) / mScaleFactor));
        }
        return pointFs;
    }

    private void touch_up() {
        mCurrentPath.mPath.lineTo(mCurrentPath.mCurrent.x, mCurrentPath.mCurrent.y);
        mCurrentPath.mEnd = new PointF(mCurrentPath.mCurrent.x, mCurrentPath.mCurrent.y);
    }


    private void touch_move(float x, float y) {
        mCurrentPath.mPath.quadTo(mCurrentPath.mCurrent.x, mCurrentPath.mCurrent.y, x, y);
        mCurrentPath.addToPath(new PointF(x, y));
    }

    private void touch_start(float x, float y) {
        mCurrentPath.mPath.reset();
        mCurrentPath.mPath.moveTo(x, y);
        mCurrentPath.mStart = new PointF(x, y);
        mCurrentPath.addToPath(mCurrentPath.mStart);
    }

    private Paint initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(100);
        return mPaint;
    }

    private Paint initPaintText() {
        Paint result = new Paint();
        result.setColor(Color.RED);
        result.setAntiAlias(true);
        result.setStyle(Paint.Style.FILL_AND_STROKE);
        result.setTextSize(7 * mScaleFactor);
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mCurrentPath.mPath, mPaint);
        if (this.mCustomPaths.size() > 0) {
            for (int i = 0; i < this.mCustomPaths.size(); i++) {
                canvas.drawPath(this.mCustomPaths.get(i).mPath, mPaints.get(i));
            }
        }
        int count = 0;
        if (this.mPresentationState != null) {
            for (int i = 0; i < mPresentationState.getAnnotations().size(); i++) {
                DICOMAnnotation ann = mPresentationState.getAnnotations().get(i);
                for (int j = 0; j < ann.getTextObjects().size(); j++) {
                    DICOMTextObject dtext = ann.getTextObjects().get(j);
                    canvas.drawCircle(this.mLeftCorner[0] + dtext.getTextAnchor().x, this.mLeftCorner[1] + dtext.getTextAnchor().y, RADIUS * mScaleFactor / 2, mPaintsText.get(count));
                    canvas.drawText(
                            dtext.getText().substring(0, dtext.getText().length() >= 20 ? 20 : dtext.getText().length()),
                            this.mLeftCorner[0] + dtext.getTextAnchor().x + RADIUS * mScaleFactor / 2, this.mLeftCorner[1] + dtext.getTextAnchor().y + RADIUS * mScaleFactor / 2, mPaintsText.get(count));
                    count++;
                }
            }
        }
    }

    public void reset(DICOMPresentationState presentationState) {
        this.mPresentationState = presentationState;
        float dx = this.mLeftCorner[0];
        float dy = this.mLeftCorner[1];
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
        this.mPaintsText = new ArrayList<Paint>();
        mPaints.add(initPaint());
        if (mPresentationState != null) {
            ArrayList<DICOMAnnotation> dicomAnnotation = (ArrayList<DICOMAnnotation>) mPresentationState.getAnnotations();
            for (int i = 0; i < dicomAnnotation.size(); i++) {

                DICOMAnnotation d = dicomAnnotation.get(i);
                for (int j = 0; j < d.getGraphicObjects().size(); j++) {
                    if (d.getGraphicObjects().get(j).getGraphicType().equals(DICOMGraphicObject.GraphicTypes.POLYLINE)) {
                        CustomPath customPath = new CustomPath();
                        customPath.paths = new ArrayList<PointF>();
                        List<PointF> points = d.getGraphicObjects().get(j).getPoints();

                        for (int p = 0; p < points.size(); p++) {
                            PointF point = points.get(p);
                            float x = (float) (dx + point.x * mScaleFactor);
                            float y = (float) (dy + point.y * mScaleFactor);
                            customPath.paths.add(new PointF(x, y));
                        }
                        customPath.mStart = customPath.paths.get(0);
                        customPath.mEnd = customPath.paths.get(customPath.paths.size() - 1);
                        customPath.mPath.reset();
                        customPath.mPath.moveTo(customPath.paths.get(0).x, customPath.paths.get(0).y);
                        for (int k = 1; k < customPath.paths.size(); k++) {
                            PointF point = customPath.paths.get(k);
                            PointF previous_point = customPath.paths.get(k - 1);
                            customPath.mPath.quadTo(previous_point.x, previous_point.y, point.x, point.y);
                        }
                        customPath.mPath.lineTo(customPath.mStart.x, customPath.mStart.y);
                        mCustomPaths.add(customPath);
                    }
                    mPaints.add(initPaint());
                }
                for (int j = 0; j < d.getTextObjects().size(); j++) {
                    mPaintsText.add(initPaintText());
                }
            }
        }
        invalidate();
    }

    public void setBounds(int imageWidth, int imageHeight, float scaleFactor, Matrix mMatix) {
        this.mImageWidth = imageWidth;
        this.mImageHeight = imageHeight;
        this.mScaleFactor = scaleFactor;
        float[] f = new float[9];
        mMatix.getValues(f);
        this.mLeftCorner = new float[]{f[2], f[5]};
    }

    private void drawIt() {
        invalidate();
    }

    private CustomPath pointInPolygonProblem(PointF currentPoint) {
        CustomPath selected = null;
        double epsilon = 0.1;
        for (int i = this.mCustomPaths.size() - 1; i >= 0; i--) {
            float sums = 0;
            for (int j = 1; j < mCustomPaths.get(i).paths.size(); j++) {
                float[] vector_i_1 = new float[2];
                float[] vector_i = new float[2];
                vector_i_1[0] = mCustomPaths.get(i).paths.get(j - 1).x - currentPoint.x;
                vector_i[0] = mCustomPaths.get(i).paths.get(j).x - currentPoint.x;
                vector_i_1[1] = mCustomPaths.get(i).paths.get(j - 1).y - currentPoint.y;
                vector_i[1] = mCustomPaths.get(i).paths.get(j).y - currentPoint.y;
                double zn = vector_i_1[0] * vector_i[1] - vector_i_1[1] * vector_i[0];
                double t1 = Math.sqrt(Math.pow(vector_i[0], 2) + Math.pow(vector_i[1], 2));
                double t2 = Math.sqrt(Math.pow(vector_i_1[0], 2) + Math.pow(vector_i_1[1], 2));
                double result = zn / (t1 * t2);
                if ((result - 1) <= epsilon && result >= 0) {
                    result = 1;
                } else if ((result - 1) > 0.2) {
                    return null;
                }
                double arccos = Math.acos(result);
                double det = vector_i_1[0] * vector_i[1] - vector_i[0] * vector_i_1[1];
                sums += arccos * Math.signum(det);
            }
            if (sums < epsilon && sums > -epsilon) {
                selected = this.mCustomPaths.remove(i);
                mPaints.remove(i + 1);

                break;
            }
        }
        return selected;
    }

    /**
     * Check the euclidean distance between all DICOMTextObjects point
     *
     * @param current <code>PointF</code>
     */
    private int[] checkPointWithText(PointF current) {
        int result[] = new int[]{-1, -1};
        for (int i = 0; i < this.mPresentationState.getAnnotations().size(); i++) {
            DICOMAnnotation ann = this.mPresentationState.getAnnotations().get(i);
            for (int j = 0; j < ann.getTextObjects().size(); j++) {
                DICOMTextObject dtext = ann.getTextObjects().get(j);
                float x = mLeftCorner[0] + dtext.getTextAnchor().x;
                float y = mLeftCorner[1] + dtext.getTextAnchor().y;
                double distance = Math.sqrt(Math.pow((current.x - x), 2) + Math.pow((current.y - y), 2));
                if (distance <= SENSITIVITY) {
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }
        return result;
    }

    private CustomPath trassByLight(PointF current) {
        CustomPath selected = null;
        for (int i = this.mCustomPaths.size() - 1; i >= 0; i--) {
            ArrayList<PointF> path = mCustomPaths.get(i).paths;
            for (int j = 1; j < path.size(); j++) {
                float x_i = path.get(j).x;
                float y_i = path.get(j).y;
                float x_i_1 = path.get(j - 1).x;
                float y_i_1 = path.get(j - 1).y;
                if (
                        ((y_i <= current.y && current.y < y_i_1) || (y_i_1 <= current.y && current.y < y_i))
                                &&
                                (current.x > (x_i_1 - x_i) * (current.y - y_i) / (y_i_1 - y_i) + x_i)
                        ) {
                    mIndexToDelete = i;
                    //selected =mCustomPaths.remove(mIndexToDelete);
                    //mPaints.remove(mIndexToDelete+1);
                    return selected = mCustomPaths.get(i);
                }
            }
        }
        return selected;
    }

    private void agrementToDelete() {
        AlertDialog.Builder buil = new AlertDialog.Builder(this.getContext());
        final TextView textView = new TextView(this.getContext());
        buil.setTitle(getResources().getString(R.string.title_for_delete_alert_dialog));
        textView.setText("Annotation:\r\n" + mCustomPaths.get(mIndexToDelete).text);
        textView.setTextSize(getResources().getInteger(R.integer.text_size_in_alertdialog));
        int d = getResources().getInteger(R.integer.padding_size_in_alertdialog);
        textView.setPadding(d * 2, d, d, d);
        buil.setView(textView);
        buil.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CustomPath deleted = mCustomPaths.remove(mIndexToDelete);
                mPaints.remove(mIndexToDelete);
                deleteFromAnnotation(deleted);
                drawIt();
                Toast t = Toast.makeText(getContext(), "Successfully deleted.", Toast.LENGTH_LONG);
                t.show();
            }
        });
        buil.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        buil.show();
    }

    private boolean deleteFromAnnotation(CustomPath deleted) {
        List<DICOMAnnotation> d = mPresentationState.getAnnotations();
        List<PointF> del = changingPoints(deleted.paths);
        for (DICOMAnnotation i : d) {
            for (DICOMGraphicObject j : i.getGraphicObjects()) {
                if (j.getGraphicType().equals(DICOMGraphicObject.GraphicTypes.POLYLINE)) {
                    boolean res = true;
                    for (int k = 0; k < Math.min(j.getPoints().size(), del.size()); k++) {
                        res &= (j.getPoints().get(k).x - del.get(k).x) < 0.01 && (j.getPoints().get(k).y - del.get(k).y) < 0.01;
                        if (!res)
                            break;
                    }
                    if (res && j.getPoints().size() == del.size()) {
                        i.getGraphicObjects().remove(j);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void agrementToDeleteTextPoint(String text, final int[] adress) {
        AlertDialog.Builder buil = new AlertDialog.Builder(this.getContext());
        final TextView textView = new TextView(this.getContext());
        buil.setTitle(getResources().getString(R.string.title_for_delete_alert_dialog));
        textView.setText("Annotation:\r\n" + text);
        textView.setTextSize(getResources().getInteger(R.integer.text_size_in_alertdialog));
        int d = getResources().getInteger(R.integer.padding_size_in_alertdialog);
        textView.setPadding(d * 2, d, d, d);
        buil.setView(textView);
        buil.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresentationState.getAnnotations().get(adress[0]).getTextObjects().remove(adress[1]);
                mPaintsText.remove(adress[0] + adress[1]);
                drawIt();
                Toast t = Toast.makeText(getContext(), "Successfully deleted.", Toast.LENGTH_LONG);
                t.show();
            }
        });
        buil.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        buil.show();
    }

    private void askForAnnotationText(final boolean modePointOrPolygon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        builder.setTitle(modePointOrPolygon ?
                getResources().getString(R.string.title_for_asking_aler_dialog)
                :
                getResources().getString(R.string.title_for_asking_text_annotation_alert_dialog)
        );
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (modePointOrPolygon)
                    mCustomPaths.get(mCustomPaths.size() - 1).text = input.getText().toString();
                else {
                    DICOMAnnotation dicomAnnotation = mPresentationState.getAnnotations().get(0);
                    DICOMTextObject t = new DICOMTextObject();
                    t.setText(input.getText().toString());
                    t.setTextAnchor(pointToSaveTextAnnotation);
                    dicomAnnotation.getTextObjects().add(t);
                    mPaintsText.add(initPaintText());
                    drawIt();
                }
                Toast toast = Toast.makeText(getContext(), "Successfully saved.", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (modePointOrPolygon) {
                    CustomPath p = mCustomPaths.remove(mCustomPaths.size() - 1);
                    mPaints.remove(mCustomPaths.size());
                    deleteFromAnnotation(p);
                    drawIt();
                } else {
                    pointToSaveTextAnnotation = null;
                }
                dialog.cancel();
            }
        });
        int c = 0;
        builder.show();
    }

    private void showtingText(CustomPath customPath2Show, boolean typePointOrPolygon, int[] adress) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getResources().getString(R.string.title_for_double_tap_aler_dialog));
        RelativeLayout rl = new RelativeLayout(getContext());

        final TextView textView = new TextView(this.getContext());
        if (typePointOrPolygon)
            textView.setText(customPath2Show.text);
        else if (adress[0] != -1 && adress[1] != -1)
            textView.setText(mPresentationState.getAnnotations().get(adress[0]).getTextObjects().
                    get(adress[1]).getText());
        else
            textView.setText("");
        textView.setTextSize(getResources().getInteger(R.integer.text_size_in_alertdialog));
        rl.addView(textView);
        int d = getResources().getInteger(R.integer.padding_size_in_alertdialog);
        rl.setPadding(2 * d, d, d, d);
        builder.setView(rl);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDoubleTap = false;
                drawIt();
                dialog.cancel();
            }
        });
        builder.show();
    }

    private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            pointToSaveTextAnnotation = new PointF(e.getX() - mLeftCorner[0], e.getY() - mLeftCorner[1]);
            askForAnnotationText(false);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!mDoubleTap) {
                CustomPath tryTofind = trassByLight(new PointF(e.getX(), e.getY()));
                if (tryTofind != null) {
                    showtingText(tryTofind, true, new int[]{-1, -1});
                    mDoubleTap = true;
                }
                int[] search = checkPointWithText(new PointF(e.getX(), e.getY()));
                if (search[0] != -1 && search[1] != -1) {
                    showtingText(tryTofind, false, search);
                    mDoubleTap = true;
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            CustomPath a = trassByLight(startPoint);//pointInPolygonProblem(startPoint);
            if (a != null) {
                agrementToDelete();
                invalidate();
            }
            int[] res = checkPointWithText(startPoint);
            if (res[0] != -1 && res[1] != -1) {
                agrementToDeleteTextPoint(
                        mPresentationState.getAnnotations().get(res[0]).getTextObjects().get(res[1])
                                .getText()
                        , res);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return false;
        }
    }

    private class CustomPath {
        public PointF mStart;
        public PointF mEnd;
        public PointF mCurrent;
        public Path mPath = new Path();
        public String text;
        private ArrayList<PointF> paths = new ArrayList<>();

        public void addToPath(PointF point) {
            mCurrent = point;
            this.paths.add(point);
        }
    }
}
