package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import be.ac.ulb.lisa.idot.android.dicomviewer.adapters.PairArrayAdapter;
import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.thread.ThreadState;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.DICOMImageView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.GrayscaleWindowView;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.dicom.file.DICOMFileFilter;
import be.ac.ulb.lisa.idot.dicom.file.DICOMImageReader;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitReader;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitWriter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DICOMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DICOMFragment extends Fragment implements View.OnTouchListener {
    private static final String FILE_NAME = "FILE_NAME";
    private static final String META_VISIBILITY = "META_VISIBILITY";

    private String mFileName;
    private GrayscaleWindowView mGrayscaleWindow;
    private DICOMImageView mImageView;                      // The image view
    private DICOMViewerData mDICOMViewerData = null;        // DICOM Viewer data
    private DICOMFileLoader mDICOMFileLoader = null;
    private LISAImageGray16Bit mImage = null;               // The LISA 16-Bit image
    private boolean mIsInitialized = false;                 //Set if the DICOM Viewer is initialized or not
    private ListView mListMetadata;                         // List view that is used to output metadata
    private PairArrayAdapter mArrayAdapter;                 // Array adapter for metadata list

    private boolean mBusy = false;
    private int mCurrentFileIndex;
    private File[] mFileArray;
    private GestureDetector mGestureDetector;
    private int mMetadataVisibility;

    public DICOMFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DICOMFragment.
     */
    public static DICOMFragment newInstance(String fileName, int metadataVisibility) {
        DICOMFragment fragment = new DICOMFragment();
        Bundle args = new Bundle();
        args.putString(FILE_NAME, fileName);
        args.putInt(META_VISIBILITY, metadataVisibility);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());
        if (getArguments() != null) {
            mFileName = getArguments().getString(FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dicom, container, false);

        mImageView = (DICOMImageView) view.findViewById(R.id.image_view);
        mGrayscaleWindow = (GrayscaleWindowView) view.findViewById(R.id.grayscale_view);
        mListMetadata = (ListView) view.findViewById(R.id.list_metadata);
        // recover file name if any
        if (savedInstanceState != null) {
            String fileName;
            fileName = savedInstanceState.getString(FILE_NAME);
            if (fileName != null)
                mFileName = fileName;
            mMetadataVisibility = savedInstanceState.getInt(META_VISIBILITY);
        }
        return view;
    }

    private void initFileLoader() {
        // load the file
        if (mFileName != null) {
            File currentFile = new File(mFileName);
            mFileArray = currentFile.getParentFile().listFiles(new DICOMFileFilter());
            // Start the loading thread to load the DICOM image
            mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler, mFileArray[mCurrentFileIndex++]);
            mDICOMFileLoader.start();
            mBusy = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileName != null)
            outState.putString(FILE_NAME, mFileName);
        outState.putInt(META_VISIBILITY, mMetadataVisibility);
    }

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

    @Override
    public void onResume() {
        super.onResume();
        //noinspection WrongConstant
        mListMetadata.setVisibility(mMetadataVisibility);
        // load the file
        initFileLoader();
        mDICOMViewerData = new DICOMViewerData();
        mDICOMViewerData.setToolMode(ToolMode.DIMENSION);
        mImageView.setDICOMViewerData(mDICOMViewerData);
        mGrayscaleWindow.setDICOMViewerData(mDICOMViewerData);
        // set adapter for a list view that is used to show metadata
        mArrayAdapter = new PairArrayAdapter(getActivity(), R.layout.metadata_item,
                R.id.metadata_tag_value, R.id.metadata_tag_key);
        mListMetadata.setAdapter(mArrayAdapter);
        mListMetadata.setDivider(null);
        mListMetadata.setDividerHeight(0);
        mListMetadata.setEnabled(false);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setMetadataVisibility(int visibility) {
        mMetadataVisibility = visibility;
        //noinspection WrongConstant
        mListMetadata.setVisibility(mMetadataVisibility);
    }

    public int getMetadataVisibility() {
        return mMetadataVisibility;
    }

    /**
     * Set the currentImage
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
            mGrayscaleWindow.setImage(mImage);
            setImageOrientation();
            // If it is not initialized, set the window width and center
            // as the value set in the LISA 16-Bit grayscale image
            // that comes from the DICOM image file.
            if (!mIsInitialized) {
                mIsInitialized = true;
                mDICOMViewerData.setWindowWidth(mImage.getWindowWidth());
                mDICOMViewerData.setWindowCenter(mImage.getWindowCenter());
                mImageView.draw();
                mImageView.fitIn();
            } else
                mImageView.draw();
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mImageView.onTouch(v, event);
        return this.mGestureDetector.onTouchEvent(event);
    }

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
        previousImage(null);
    }

    public void onSwipeLeft() {
        mBusy = false;
        nextImage(null);
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    /**
     * Handle touch on the previousButton.
     *
     * @param view
     */
    public synchronized void previousImage(View view) {

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
        // If the current file index is 0, there is
        // no previous file in the files array
        // We add the less or equal to zero because it is
        // safer
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
        //  Decrease the file index
        mCurrentFileIndex--;
        // Start the loading thread to load the DICOM image
        mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler,
                mFileArray[mCurrentFileIndex]);
        mDICOMFileLoader.start();
        // Update the UI
        // mIndexTextView.setText(String.valueOf(mCurrentFileIndex + 1));
        //mIndexSeekBar.setProgress(mCurrentFileIndex);

        // if (mCurrentFileIndex == 0)
        //  mPreviousButton.setVisibility(View.INVISIBLE);

        // The next button is automatically set to visible
        // because if there is a previous image, there is
        // a next image
        // mNextButton.setVisibility(View.VISIBLE);
    }

    /**
     * Handle touch on next button.
     *
     * @param view
     */
    public synchronized void nextImage(View view) {
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
        //  Increase the file index
        mCurrentFileIndex++;
        // Start the loading thread to load the DICOM image
        mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler,
                mFileArray[mCurrentFileIndex]);
        mDICOMFileLoader.start();
        // Update the UI
        //mIndexTextView.setText(String.valueOf(mCurrentFileIndex + 1));
        //mIndexSeekBar.setProgress(mCurrentFileIndex);
        //if (mCurrentFileIndex == (mFileArray.length - 1))
        //mNextButton.setVisibility(View.INVISIBLE);
        // The previous button is automatically set to visible
        // because if there is a next image, there is
        // a previous image
        //mPreviousButton.setVisibility(View.VISIBLE);

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
                    if (message.obj instanceof DICOMMetaInformation) {
                        mArrayAdapter.clear();
                        // output information from metadata
                        Resources resources = getResources();
                        DICOMMetaInformation metaInformation = (DICOMMetaInformation) message.obj;
                        String keyName = resources.getString(R.string.metadata_name),
                                keyBirthDate = resources.getString(R.string.metadata_birth_date),
                                keyAge = resources.getString(R.string.metadata_age);
                        String name = metaInformation.getPaitentName(),
                                age = metaInformation.getPaitentAge(),
                                birthDate = metaInformation.getPaitentBirthDate();
                        String anonymous = resources.getString(R.string.metadata_anonymous);
                        mArrayAdapter.add(new Pair<>(keyName, processAttribute(name, anonymous)));
                        mArrayAdapter.add(new Pair<>(keyAge, processAttribute(age, anonymous)));
                        mArrayAdapter.add(new Pair<>(keyBirthDate, processAttribute(birthDate, anonymous)));
                    }
                    if (message.obj == null) {
                        mArrayAdapter.clear();
                        mArrayAdapter.add(new Pair<>("No metadata available", "No metadata available"));
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
            // If image exists show image
            try {
                LISAImageGray16BitReader reader =
                        new LISAImageGray16BitReader(mFile + ".lisa");

                LISAImageGray16Bit image = reader.parseImage();
                reader.close();
                DICOMImageReader dicomFileReader = new DICOMImageReader(mFile);
                DICOMImage dicomImage = dicomFileReader.parse();
                readMetadata(dicomImage);
                dicomFileReader.close();
                // Send the LISA 16-Bit grayscale image
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.FINISHED;
                message.obj = image;
                mHandler.sendMessage(message);
                return;
            } catch (Exception ex) {
                // Do nothing and create a LISA image
            }
            // Create a LISA image and ask to show the
            // progress dialog in spinner mode
            mHandler.sendEmptyMessage(ThreadState.STARTED);
            try {
                DICOMImageReader dicomFileReader = new DICOMImageReader(mFile);
                DICOMImage dicomImage = dicomFileReader.parse();
                readMetadata(dicomImage);
                dicomFileReader.close();

                Message message;
                // If the image is uncompressed, show it and cached it.
                if (dicomImage.isUncompressed()) {
                    LISAImageGray16BitWriter out =
                            new LISAImageGray16BitWriter(mFile + ".lisa");

                    out.write(dicomImage.getImage());
                    out.flush();
                    out.close();

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
            } catch (OutOfMemoryError ex) {
                File fCleanup = new File(mFile.getAbsolutePath() + ".lisa");
                fCleanup.delete();
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.OUT_OF_MEMORY;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);
            } catch (Exception ex) {
                File fCleanup = new File(mFile.getAbsolutePath() + ".lisa");
                fCleanup.delete();
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.UNCATCHABLE_ERROR_OCCURRED;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);
            }
        }
    }
}
