package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import be.ac.ulb.lisa.idot.android.dicomviewer.adapters.PairArrayAdapter;
import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.thread.ThreadState;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.AnnotationView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.DICOMImageView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.AreaView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.ProtractorView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.RulerView;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMPresentationState;
import be.ac.ulb.lisa.idot.dicom.file.DICOMFileFilter;
import be.ac.ulb.lisa.idot.dicom.file.DICOMImageReader;
import be.ac.ulb.lisa.idot.dicom.file.DICOMPresentationStateReader;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DICOMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("WrongConstant")
public class DICOMFragment extends Fragment implements View.OnTouchListener {
    /**
     * Constants which are used to save/recover state of the fragment.
     */
    public static final String FILE_NAME = "FILE_NAME";
    public static final String META_VISIBILITY = "META_VISIBILITY";
    public static final String FILE_INDEX = "FILE_INDEX";
    public static final String CURRENT_TOOL = "CURRENT_TOOL";
    public static final String SCALE_FACTOR = "SCALE_FACTOR";

    /**
     * Tools available on the fragment.
     */
    public interface Tool {
        int NONE = 0;
        int RULER = 1;
        int PROTRACTOR = 2;
        int AREA = 3;
        int ANNOTATIONS = 4;
    }

    private String mFileName;
    //    private GrayscaleWindowView mGrayscaleWindow;
    private View.OnTouchListener mTouchListener;            // Current view that is interacting with user
    private RulerView mRulerView;                           // The image view without any decorators
    private ProtractorView mProtractorView;                 // The image view without any decorators with protractor functionality
    private DICOMImageView mImageView;                      // The image view with decorators (tools)
    private AreaView mAreaView;                  // the image view with drawing the figure
    private AnnotationView mAnnotationView;                 // the annotation view with drawing figure with text for annotations
    private DICOMViewerData mDICOMViewerData = null;        // DICOM Viewer data
    private DICOMFileLoader mDICOMFileLoader = null;
    private DICOMPresentationState mPresentationState = null;// Presentation State of the file (Contains annotations)
    private LISAImageGray16Bit mImage = null;               // The LISA 16-Bit image
    private boolean mIsInitialized = false;                 //Set if the DICOM Viewer is initialized or not
    private ListView mListMetadata;                         // List view that is used to output metadata
    private PairArrayAdapter mArrayAdapter;                 // Array adapter for metadata list

    private boolean mBusy = false;
    private int mCurrentFileIndex = -1;          // index of the currently opened file in the directory
    private File[] mFileArray;                  // list of the files in series
    private int mMetadataVisibility;            // visibility state of the list with metadata
    private int mCurrentTool;                   // currently selected tool
    private float mScaleFactor;                 // scale factor of the image view
    private GestureDetector mGestureDetector;   // gesture detector which is responsible for swap operations
    private int mScreenOrientation;             // saved state of the screen orientation

    /**
     * This method is used to create a new instance of the fragment.
     *
     * @param fileName the name of the image file that will be shown.
     * @return
     */
    public static DICOMFragment newInstance(String fileName) {
        DICOMFragment fragment = new DICOMFragment();
        Bundle args = new Bundle();
        args.putString(FILE_NAME, fileName);
        args.putInt(META_VISIBILITY, View.INVISIBLE);
        args.putInt(FILE_INDEX, 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        // recover file name if any
        Bundle args = savedInstanceState == null ? getArguments() : savedInstanceState;
        restoreInstanceState(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dicom, container, false);

        mCurrentFileIndex = -1;
        mMetadataVisibility = View.INVISIBLE;

        mRulerView = (RulerView) view.findViewById(R.id.ruler_view);
        mRulerView.setVisibility(View.GONE);

        mProtractorView = (ProtractorView) view.findViewById(R.id.protractor_view);
        mProtractorView.setVisibility(View.GONE);
        mAreaView = (AreaView) view.findViewById(R.id.area_view);
        mAreaView.setVisibility(View.GONE);
        mAnnotationView = (AnnotationView) view.findViewById(R.id.annotation_view);
        mAnnotationView.setVisibility(View.GONE);
        mImageView = (DICOMImageView) view.findViewById(R.id.image_view);
        mTouchListener = mImageView;
        // set adapter for a list view that is used to show metadata
        mArrayAdapter = new PairArrayAdapter(getActivity(), R.layout.metadata_item,
                R.id.metadata_tag_value, R.id.metadata_tag_key);
        mListMetadata = (ListView) view.findViewById(R.id.list_metadata);
        mListMetadata.setOnTouchListener(this);
        mListMetadata.setAdapter(mArrayAdapter);
        // recover file name if any
        restoreInstanceState(savedInstanceState);
        setTool(mCurrentTool);
        return view;
    }

    /**
     * Load image from the specified file and create a list of images in current directory.
     */
    private void initFileLoader() {
        // load the file
        if (mFileName != null) {
            File currentFile = new File(mFileName);
            mFileArray = currentFile.getParentFile().listFiles(new DICOMFileFilter());
            // If we have not loaded any file from the current directory
            // the compute the index of the current file
            if (mCurrentFileIndex < 0)
                mCurrentFileIndex = Arrays.asList(mFileArray).indexOf(currentFile);
            // Start the loading thread to load the DICOM image
            mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler, mFileArray[mCurrentFileIndex]);
            mDICOMFileLoader.start();
            mBusy = true;
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILE_NAME, mFileName);
        outState.putInt(META_VISIBILITY, mMetadataVisibility);
        outState.putInt(FILE_INDEX, mCurrentFileIndex);
        outState.putInt(CURRENT_TOOL, mCurrentTool);
        outState.putFloat(SCALE_FACTOR, mImageView.getScaleFactor());
    }

    /**
     * Called to restore the state of the fragment from the specified bundle.
     *
     * @param bundle
     */
    private void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(FILE_NAME))
                mFileName = bundle.getString(FILE_NAME);
            if (bundle.containsKey(META_VISIBILITY))
                mMetadataVisibility = bundle.getInt(META_VISIBILITY);
            if (bundle.containsKey(FILE_INDEX))
                mCurrentFileIndex = bundle.getInt(FILE_INDEX);
            if (bundle.containsKey(CURRENT_TOOL))
                mCurrentTool = bundle.getInt(CURRENT_TOOL);
            if (bundle.containsKey(SCALE_FACTOR))
                mScaleFactor = bundle.getFloat(SCALE_FACTOR);
        }
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mImage = null;
        mDICOMViewerData = null;
        mDICOMFileLoader = null;
        // Free the drawable callback
        if (mImageView != null) {
            Drawable drawable = mImageView.getDrawable();
            if (drawable != null)
                drawable.setCallback(null);
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        // load the file
        initFileLoader();
        mIsInitialized = false;
        mDICOMViewerData = new DICOMViewerData();
        mDICOMViewerData.setToolMode(ToolMode.DIMENSION);
        if (mScaleFactor > mImageView.getScaleFactor())
            mImageView.setScaleFactor(mScaleFactor);
        mImageView.setDICOMViewerData(mDICOMViewerData);
        mListMetadata.setDivider(null);
        mListMetadata.setDividerHeight(0);
        mListMetadata.setVisibility(mMetadataVisibility);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScaleFactor = mImageView.getScaleFactor();
    }

    /**
     * @param visibility Sets the visibility of the metadata list according to this variable.
     */
    public void setMetadataVisibility(int visibility) {
        mMetadataVisibility = visibility;
        mListMetadata.setVisibility(mMetadataVisibility);
    }

    /**
     * @return Current visibility of the metadata list.
     */
    public int getMetadataVisibility() {
        return mMetadataVisibility;
    }

    /**
     * Sets the tool of the fragment. Possible values: NONE, RULER, PROTRACTOR, AREA.
     *
     * @param tool
     */
    public void setTool(int tool) {
        if (mCurrentTool == Tool.NONE)
            mScreenOrientation = getActivity().getRequestedOrientation();
        mCurrentTool = tool;
        if (mCurrentTool == Tool.NONE)
            getActivity().setRequestedOrientation(mScreenOrientation);
        else
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        mRulerView.setVisibility(View.GONE);
        mProtractorView.setVisibility(View.GONE);
        mAreaView.setVisibility(View.GONE);
        mAnnotationView.setVisibility(View.GONE);
        switch (tool) {
            case Tool.RULER:
                mTouchListener = mRulerView;
                mRulerView.reset();
                mRulerView.setScaleFactor(mImageView.getScaleFactor());
                mRulerView.setVisibility(View.VISIBLE);
                break;
            case Tool.PROTRACTOR:
                mTouchListener = mProtractorView;
                mProtractorView.reset();
                mProtractorView.setVisibility(View.VISIBLE);
                break;
            case Tool.AREA:
                mTouchListener = mAreaView;
                mAreaView.reset();
                mAreaView.setVisibility(View.VISIBLE);
                mAreaView.setScaleFactor(mImageView.getScaleFactor());
                break;
            case Tool.ANNOTATIONS:
                mTouchListener = mAnnotationView;
                mAnnotationView.setVisibility(View.VISIBLE);
                mAnnotationView.setBounds(mImage.getWidth(),mImage.getHeight(),mImageView.getScaleFactor(), mImageView.getMatrix());
                mAnnotationView.reset(mPresentationState);
                break;
            default:
                mTouchListener = mImageView;
                break;
        }
    }

    /**
     * @return Currently selected tool.
     */
    public int getTool() {
        return mCurrentTool;
    }


    /**
     * Set the current image.
     *
     * @param image
     */
    private void setImage(LISAImageGray16Bit image) {
        if (image == null)
            throw new NullPointerException("The LISA 16-Bit grayscale image " +
                    "is null");
        try {
            // Set the image
            mImage = null;
            mImage = image;
            mImageView.setImage(mImage);
            mImageView.setOnTouchListener(this);
            setImageOrientation();
            // If it is not initialized, set the window width and center
            if (!mIsInitialized) {
                mIsInitialized = true;
                mDICOMViewerData.setWindowWidth(mImage.getWindowWidth());
                mDICOMViewerData.setWindowCenter(mImage.getWindowCenter());
                mDICOMViewerData.setDefaultCenter(mImage.getWindowCenter());
                mDICOMViewerData.setDefaultWidth(mImage.getWindowWidth());
                mImageView.draw();
            } else
                mImageView.draw();
            float measuredFactor = mImageView.getMeasuredWidth() / mImageView.getWidth();
            if (mScaleFactor <= measuredFactor)
                mImageView.fitIn();
            else
                mImageView.center();
            mBusy = false;
        } catch (OutOfMemoryError ex) {
            System.gc();
            showExitAlertDialog("[ERROR] Out Of Memory",
                    "This series contains images that are too big" +
                            " and that cause out of memory error. The best is to don't" +
                            " use the series seek bar. If the error occurs again" +
                            " it is because this series is not adapted to your" +
                            " Android(TM) device.");

        } catch (ArrayIndexOutOfBoundsException ex) {
            showExitAlertDialog("[ERROR] Image drawing",
                    "An uncatchable error occurs while " +
                            "drawing the DICOM image.");
        }
    }

    /**
     * Set the image orientation TextViews
     */
    private void setImageOrientation() {
        float[] imageOrientation = mImage.getImageOrientation();
        if (imageOrientation == null
                || imageOrientation.length != 6
                || imageOrientation.equals(new float[6])) // equal to a float with 6 zeros
            return;
        // Displaying the row orientation
//        mRowOrientation.setText(getImageOrientationString(imageOrientation, 0));
        // Displaying the column orientation
//        mColumnOrientation.setText(getImageOrientationString(imageOrientation, 3));
    }

    /**
     * Show an alert dialog (AlertDialog) to inform
     * the user that the activity must finish.
     *
     * @param title   Title of the AlertDialog.
     * @param message Message of the AlertDialog.
     */
    private void showExitAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DICOMFragment.this.getActivity().finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Listener of the touch events.
     *
     * @param v     view which received the touch event.
     * @param event detailed information about touch event.
     * @return true if touch event was successfully processed.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean res = mTouchListener.onTouch(v, event);
        // if current touch listener is some tool, then we are processing
        // swipes and other interaction with the user, i.e. only interaction
        // with tool is processed.
        if (mTouchListener != mImageView)
            return res;
        return this.mGestureDetector.onTouchEvent(event);
    }

    /**
     * Gesture listener of the fragment.
     * Switching images of the view.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            //return mImageView.onTouch(null,e);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        mBusy = false;
        previousImage();
    }

    public void onSwipeLeft() {
        mBusy = false;
        nextImage();
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    /**
     * Handle touch on the previousButton.
     */
    public synchronized void previousImage() {
        // If it is busy, do nothing
        if (mBusy)
            return;
        // It is busy now
        mBusy = true;
        // Wait until the loading thread die
        while (mDICOMFileLoader.isAlive()) {
            try {
                synchronized (this) {
                    wait(10);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        // If the current file index is 0, there is no previous file in the files array
        // We add the less or equal to zero because it is safer
        if (mCurrentFileIndex <= 0) {
            // Not necessary but safer, because we don't know
            // how the code will be used in the future
            mCurrentFileIndex = 0;
            // If for a unknown reason the previous button is
            // visible => hide it
            //if (mPreviousButton.getVisibility() == View.VISIBLE)
            //mPreviousButton.setVisibility(View.INVISIBLE);
            mBusy = false;
            return;
        }
        mScaleFactor = 0f;
        //  Decrease the file index
        mCurrentFileIndex--;
        // Start the loading thread to load the DICOM image
        mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler,
                mFileArray[mCurrentFileIndex]);
        mDICOMFileLoader.start();
    }

    /**
     * Handle touch on next button.
     */
    public synchronized void nextImage() {
        // If it is busy, do nothing
        if (mBusy)
            return;
        // It is busy now
        mBusy = true;
        // Wait until the loading thread die
        while (mDICOMFileLoader.isAlive()) {
            try {
                synchronized (this) {
                    wait(10);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        // If the current file index is the last file index,
        // there is no next file in the files array
        // We add the greater or equal to (mFileArray.length - 1)
        // because it is safer
        if (mCurrentFileIndex >= (mFileArray.length - 1)) {
            // Not necessary but safer, because we don't know
            // how the code will be used in the future
            mCurrentFileIndex = (mFileArray.length - 1);
            // If for a unknown reason the previous button is
            // visible => hide it
            //if (mNextButton.getVisibility() == View.VISIBLE)
            //mNextButton.setVisibility(View.INVISIBLE);
            mBusy = false;
            return;
        }
        mScaleFactor = 0f;
        //  Increase the file index
        mCurrentFileIndex++;
        // Start the loading thread to load the DICOM image
        mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler,
                mFileArray[mCurrentFileIndex]);
        mDICOMFileLoader.start();
    }


    private final Handler mLoadingHandler = new Handler() {

        private String processAttribute(String attribute, String substitue) {
            if (attribute == null ||
                    attribute.trim().length() == 0)
                return substitue;
            return attribute;
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case ThreadState.STARTED:
//                    showDialog(PROGRESS_DIALOG_LOAD);
                    break;
                case ThreadState.PROGRESSION_UPDATE:
                    if (message.obj == null) {
                        mArrayAdapter.clear();
                        mArrayAdapter.add(new Pair<>("No metadata available", "No metadata available"));
                    }
                    if (message.obj instanceof DICOMMetaInformation) {
                        mArrayAdapter.clear();
                        // output information from metadata
                        Resources resources = getResources();
                        DICOMMetaInformation metaInformation = (DICOMMetaInformation) message.obj;
                        float[] pixelSpasing = metaInformation.getPixelSpacing();
                        mImageView.setPixelSpacing(pixelSpasing);

                        mRulerView.setPixelSpacing(pixelSpasing);
                        mProtractorView.setPixelSpacing(pixelSpasing);
                        mAreaView.setPixelSpacing(pixelSpasing);
                        String keyName = resources.getString(R.string.metadata_name),
                                keyBirthDate = resources.getString(R.string.metadata_birth_date),
                                keyAge = resources.getString(R.string.metadata_age);
                        String name = metaInformation.getPatientName(),
                                age = metaInformation.getPatientAge(),
                                birthDate = metaInformation.getPatientBirthDate();
                        String anonymous = resources.getString(R.string.metadata_anonymous);
                        mArrayAdapter.add(new Pair<>(keyName, processAttribute(name, anonymous)));
                        mArrayAdapter.add(new Pair<>(keyAge, processAttribute(age, anonymous)));
                        mArrayAdapter.add(new Pair<>(keyBirthDate, processAttribute(birthDate, anonymous)));
                    } else if (message.obj instanceof DICOMPresentationState) {
                        mPresentationState = (DICOMPresentationState) message.obj;
                    }
                    break;
                case ThreadState.FINISHED:
                    // Set the loaded image
                    if (message.obj instanceof LISAImageGray16Bit)
                        setImage((LISAImageGray16Bit) message.obj);
                    break;
                case ThreadState.UNCATCHABLE_ERROR_OCCURRED:
                    // Get the error message
                    String errorMessage;
                    if (message.obj instanceof String)
                        errorMessage = (String) message.obj;
                    else
                        errorMessage = "Unknown error";
                    // Show an alert dialog
                    showExitAlertDialog("[ERROR] Loading file",
                            "An error occured during the file loading.\n\n"
                                    + errorMessage);
                    break;
                case ThreadState.OUT_OF_MEMORY:
                    // Show an alert dialog
                    showExitAlertDialog("[ERROR] Loading file",
                            "OutOfMemoryError: During the loading of image ("
                                    + mFileName
                                    + "), an out of memory error occurred.\n\n"
                                    + "Your file is too large for your Android system. You can"
                                    + " try to cache the image in the file chooser."
                                    + " If the error occured again, then the image cannot be displayed"
                                    + " on your device.\n"
                                    + "Try to use the Droid Dicom Viewer desktop file cacher software"
                                    + " (not available yet).");
                    break;
            }

        }

    };

    /**
     * @param valueCenter
     * @param valueWidth
     */
    public void setImageCenter(int valueCenter, int valueWidth) {
        if (valueCenter == -2 && valueWidth == -2) {
            this.mDICOMViewerData.setWindowCenter(mDICOMViewerData.getDefaultCenter());
            this.mDICOMViewerData.setWindowWidth(mDICOMViewerData.getDefaultWidth());
        } else {
            this.mDICOMViewerData.setWindowCenter(valueCenter);
            this.mDICOMViewerData.setWindowWidth(valueWidth);
        }
        this.mImageView.draw();
    }

    private static final class DICOMFileLoader extends Thread {

        // The handler to send message to the parent thread
        private final Handler mHandler;

        // The file to load
        private final File mFile;

        public DICOMFileLoader(Handler handler, File file) {
            if (handler == null)
                throw new NullPointerException("The handler is null while" +
                        " calling the loading thread.");
            mHandler = handler;
            if (file == null)
                throw new NullPointerException("The file is null while" +
                        " calling the loading thread.");
            mFile = file;
        }

        /***
         * If there is meta information, then read it and update
         *
         * @return metadata that was read from file or null in case there no metadata or error
         * occurred.
         */
        private DICOMMetaInformation readMetadata(DICOMImage image) {
            DICOMMetaInformation metaInformation = null;
            Message message = mHandler.obtainMessage();
            message.what = ThreadState.PROGRESSION_UPDATE;
            if (image.hasMetaInformation())
                metaInformation = image.getMetaInformation();
            message.obj = metaInformation;
            mHandler.sendMessage(message);
            return metaInformation;
        }

        public void run() {
            // If the image data is null, do nothing.
            if (!mFile.exists()) {
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.UNCATCHABLE_ERROR_OCCURRED;
                message.obj = "The file doesn't exist.";
                mHandler.sendMessage(message);
                return;
            }
            // This part of the code reads presentations state information of the
            // DICOM file. It includes annotations.
            DICOMPresentationState presentationState = null;
            try {
                File presentationFile = new File(mFile.getCanonicalPath() + ".ps");
                if (presentationFile.exists()) {
                    DICOMPresentationStateReader stateReader;
                    stateReader = new DICOMPresentationStateReader(presentationFile);
                    presentationState = stateReader.parse();
                    stateReader.close();
                }
            } catch (IOException | DICOMException ex) {
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.UNCATCHABLE_ERROR_OCCURRED;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);
            }
            Message message;
            DICOMImage dicomImage;
            DICOMImageReader dicomFileReader;
            // Create a LISA image and ask to show the
            // progress dialog in spinner mode
            mHandler.sendEmptyMessage(ThreadState.STARTED);
            try {
                dicomFileReader = new DICOMImageReader(mFile);
                dicomImage = dicomFileReader.parse();
                readMetadata(dicomImage);
                dicomFileReader.close();
                // Instantiate presentation state object.
                message = mHandler.obtainMessage();
                message.what = ThreadState.PROGRESSION_UPDATE;
                if (presentationState == null)
                    presentationState = new DICOMPresentationState(dicomImage);
                message.obj = presentationState;
                mHandler.sendMessage(message);
                // If the image is uncompressed, show it and cached it.
                if (dicomImage.isUncompressed()) {
                    message = mHandler.obtainMessage();
                    message.what = ThreadState.FINISHED;
                    message.obj = dicomImage.getImage();
                    mHandler.sendMessage(message);
                    // Hint the garbage collector
                    System.gc();
                } else {
                    message = mHandler.obtainMessage();
                    message.what = ThreadState.UNCATCHABLE_ERROR_OCCURRED;
                    message.obj = "The file is compressed. Compressed format are not"
                            + " supported yet.";
                    mHandler.sendMessage(message);
                }
            } catch (OutOfMemoryError | Exception ex) {
                File fCleanup = new File(mFile.getAbsolutePath() + ".lisa");
                fCleanup.delete();
                message = mHandler.obtainMessage();
                message.what = ThreadState.OUT_OF_MEMORY;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);
            }
        }
    }
}

