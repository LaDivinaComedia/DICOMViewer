package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.dicom.data.DICOMAnnotation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMGraphicObject;
import be.ac.ulb.lisa.idot.dicom.data.DICOMPresentationState;

/**
 * Created by vvanichkov on 24.10.2016.
 */

public class AnnotationView extends ToolView implements View.OnTouchListener{
//    private ArrayList<Paint> mPaints = new ArrayList<Paint>();
    private ArrayList<CustomPath> mCustomPaths = new ArrayList<CustomPath>();
    private ArrayList<Paint> mPaints;
    private CustomPath mCurrentPath;
    private float mScaleFactor;
    private int mImageWidth;
    private int mImageHeight;
    private boolean inbound = true;
    private PointF startPoint = null;
    private boolean lateStart = true;
    private int mIndexToDelete;
    private GestureDetector mGestureDetector = new GestureDetector(this.getContext(),new GestureListener());
    private boolean mDoubleTap = false;
    private DICOMPresentationState mPresentationState;

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
    protected void init(){
        mCurrentPath = new CustomPath();
        //initPaint();
        if(mPaints==null)
            mPaints = new ArrayList<Paint>();
        mPaints.add(initPaint());
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = this.getWidth()/2;
        float centerY = this.getHeight()/2;
        if((centerX-this.mImageWidth*this.mScaleFactor/2)>x || (centerX+this.mImageWidth*this.mScaleFactor/2)<x
                ||
                (centerY-this.mImageHeight*this.mScaleFactor/2)>y || (centerY+this.mImageHeight*this.mScaleFactor/2)<y
                ){
            if((centerX-this.mImageWidth*this.mScaleFactor/2)>x)
                x=centerX-this.mImageWidth*this.mScaleFactor/2;
            else if((centerX+this.mImageWidth*this.mScaleFactor/2)<x)
                x=(centerX+this.mImageWidth*this.mScaleFactor/2);
            if((centerY-this.mImageHeight*this.mScaleFactor/2)>y)
                y=(centerY-this.mImageHeight*this.mScaleFactor/2);
            else if((centerY+this.mImageHeight*this.mScaleFactor/2)<y)
                y=(centerY+this.mImageHeight*this.mScaleFactor/2);
        }

        switch (event.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(this.startPoint==null){
                    startPoint = new PointF(event.getX(),event.getY());
                }
                if(!lateStart){
                    touch_start(x, y);
                    this.invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(!inbound){
                    inbound=true;
                    //init();
                    touch_start(x,y);
                }
                if(lateStart){
                    touch_start(x, y);
                    lateStart = false;
                }
                touch_move(x, y);
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                double distnce= Math.sqrt(Math.pow(startPoint.x-x,2)+Math.sqrt(Math.pow(startPoint.y-y,2)));
                if(distnce>10 && mCurrentPath.paths.size()>5){
                    touch_up();
                    mCurrentPath.mPath.lineTo(mCurrentPath.mStart.x,mCurrentPath.mStart.y);
                    askForAnnotationText();
                    this.invalidate();
                    this.mCustomPaths.add(this.mCurrentPath);
                    init();
                }//else{
                    //trassByLight(startPoint);//pointInPolygonProblem(startPoint);
                    //invalidate();

                //}
                startPoint=null;
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void touch_up() {
        mCurrentPath.mPath.lineTo(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y);
        mCurrentPath.mEnd = new PointF(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y);
    }


    private void touch_move(float x, float y) {
        mCurrentPath.mPath.quadTo(mCurrentPath.mCurrent.x,mCurrentPath.mCurrent.y,x,y);
        mCurrentPath.addToPath(new PointF(x,y));
    }

    private void touch_start(float x, float y) {
        mCurrentPath.mPath.reset();
        mCurrentPath.mPath.moveTo(x,y);
        mCurrentPath.mStart = new PointF(x,y);
        mCurrentPath.addToPath(mCurrentPath.mStart);
    }
    private Paint initPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAlpha(100);
        return mPaint;
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPath(mCurrentPath.mPath,mPaint);
        if(this.mCustomPaths.size()>0){
            for(int i=0;i<this.mCustomPaths.size();i++){
                canvas.drawPath(this.mCustomPaths.get(i).mPath,mPaints.get(i));
            }
        }
    }

    public void reset(DICOMPresentationState presentationState){
        //TODO loading data from tag of annotation if it exists for new image.
        this.mPresentationState = presentationState;
        mCustomPaths = new ArrayList<CustomPath>();
        mPaints = new ArrayList<Paint>();
        mPaints.add(initPaint());
        if(mPresentationState!=null){
            ArrayList<DICOMAnnotation> dicomAnnotation = (ArrayList<DICOMAnnotation>) mPresentationState.getAnnotations();
            for(int i=0;i<dicomAnnotation.size();i++){

                DICOMAnnotation d = dicomAnnotation.get(i);
                for(int j = 0;j<d.getGraphicObjects().size();j++){
                    if(d.getGraphicObjects().get(j).getGraphicType().equals(DICOMGraphicObject.GraphicTypes.POLYLINE)){
                        CustomPath customPath = new CustomPath();
                        customPath.paths=(ArrayList<PointF>) d.getGraphicObjects().get(j).getPoints();
                        customPath.text=d.getTextObjects().get(j).getText();
                        customPath.mStart  = customPath.paths.get(0);
                        customPath.mEnd  = customPath.paths.get(customPath.paths.size()-1);
                        customPath.mPath.reset();
                        for(int k = 0; k < customPath.paths.size();k++){
                            PointF point = customPath.paths.get(k);
                            customPath.mPath.moveTo(point.x,point.y);
                        }
                        mCustomPaths.add(customPath);
                    }
                }

            }
        }

    }
    public void setBounds(int imageWidth, int imageHeight, float scaleFactor){
        this.mImageWidth = imageWidth;
        this.mImageHeight = imageHeight;
        this.mScaleFactor = scaleFactor;

    }
    private void drawIt(){
        invalidate();
    }
    private CustomPath pointInPolygonProblem(PointF currentPoint){
        CustomPath selected = null;
        double epsilon=0.1;
        for(int i=this.mCustomPaths.size()-1;i>=0;i--){
            float sums = 0;
            for(int j=1;j<mCustomPaths.get(i).paths.size();j++){
                float[] vector_i_1 = new float[2];
                float[] vector_i = new float[2];
                vector_i_1[0] = mCustomPaths.get(i).paths.get(j-1).x-currentPoint.x;
                vector_i[0] = mCustomPaths.get(i).paths.get(j).x-currentPoint.x;
                vector_i_1[1] = mCustomPaths.get(i).paths.get(j-1).y-currentPoint.y;
                vector_i[1] = mCustomPaths.get(i).paths.get(j).y-currentPoint.y;
                double zn = vector_i_1[0]*vector_i[1]-vector_i_1[1]*vector_i[0];
                double t1 = Math.sqrt(Math.pow(vector_i[0],2)+Math.pow(vector_i[1],2));
                double t2 = Math.sqrt(Math.pow(vector_i_1[0],2)+Math.pow(vector_i_1[1],2));
                double result = zn/(t1*t2);
                if ((result-1)<=epsilon && result>=0){
                    result = 1;
                }else if((result-1)>0.2){
                    return null;
                }
                double arccos = Math.acos(result);
                double det = vector_i_1[0]*vector_i[1]-vector_i[0]*vector_i_1[1];
                sums+=arccos*Math.signum(det);
            }
            if(sums<epsilon && sums>-epsilon){
                selected = this.mCustomPaths.remove(i);
                mPaints.remove(i+1);

                break;
            }
        }
        return selected;
    }

    private CustomPath trassByLight(PointF current){
        CustomPath selected = null;
        for(int i=this.mCustomPaths.size()-1;i>=0;i--){
            ArrayList<PointF> path = mCustomPaths.get(i).paths;
            for(int j=1;j<path.size();j++){
                float x_i = path.get(j).x;
                float y_i = path.get(j).y;
                float x_i_1 = path.get(j-1).x;
                float y_i_1 = path.get(j-1).y;
                if(
                        ((y_i<=current.y && current.y <y_i_1) || (y_i_1<=current.y && current.y<y_i))
                        &&
                         (current.x>(x_i_1-x_i)*(current.y-y_i)/(y_i_1-y_i)+x_i)
                  ){
                    mIndexToDelete=i;
                    //selected =mCustomPaths.remove(mIndexToDelete);
                    //mPaints.remove(mIndexToDelete+1);
                    return selected = mCustomPaths.get(i);
                }
            }
        }
        return selected;
    }
    private void agrementToDelete(){
        AlertDialog.Builder buil = new AlertDialog.Builder(this.getContext());
        final TextView textView = new TextView(this.getContext());
        buil.setTitle(getResources().getString(R.string.title_for_delete_alert_dialog));
        textView.setText("Annotation:\r\n"+mCustomPaths.get(mIndexToDelete).text);
        textView.setTextSize(12);
        buil.setView(textView);
        buil.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCustomPaths.remove(mIndexToDelete);
                mPaints.remove(mIndexToDelete+1);
                drawIt();
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

    private void askForAnnotationText(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getResources().getString(R.string.title_for_asking_aler_dialog));
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCustomPaths.get(mCustomPaths.size()-1).text = input.getText().toString();
                System.out.println(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCustomPaths.remove(mCustomPaths.size()-1);
                mPaints.remove(mCustomPaths.size());
                drawIt();
                dialog.cancel();
            }
        } );
        builder.show();
    }

    private void showtingText(CustomPath customPath2Show){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(getResources().getString(R.string.title_for_double_tap_aler_dialog));
        final TextView textView = new TextView(this.getContext());
        textView.setText(customPath2Show.text);
        builder.setView(textView);
        builder.setNegativeButton("OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDoubleTap=false;
                drawIt();
                dialog.cancel();
            }
        } );
        builder.show();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTapEvent(MotionEvent event){
            if(!mDoubleTap){
                CustomPath tryTofind = trassByLight(new PointF(event.getX(),event.getY()));
                if(tryTofind!=null){
                    showtingText(tryTofind);
                    mDoubleTap=true;
                }
            }
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent event){

            return true;
        }
        @Override
        public void onLongPress(MotionEvent e){
            trassByLight(startPoint);//pointInPolygonProblem(startPoint);
            agrementToDelete();
            invalidate();
        }
    }
    private class CustomPath{
        public PointF mStart;
        public PointF mEnd;
        public PointF mCurrent;
        public Path mPath = new Path();
        public String text;
        private ArrayList<PointF> paths = new ArrayList<>();

        public void addToPath(PointF point) {
            mCurrent=point;
            this.paths.add(point);
        }
    }
}
