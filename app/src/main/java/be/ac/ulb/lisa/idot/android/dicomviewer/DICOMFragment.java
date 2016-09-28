package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.thread.ThreadState;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.DICOMImageView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.GrayscaleWindowView;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.dicom.file.DICOMImageReader;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitReader;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitWriter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DICOMFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DICOMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DICOMFragment extends Fragment {
    private static final String FILE_NAME = "FILE_NAME";

    private String mFileName;
    private GrayscaleWindowView mGrayscaleWindow;
    private DICOMImageView mImageView;                      // The image view
    private DICOMViewerData mDICOMViewerData = null;        // DICOM Viewer data
    private DICOMFileLoader mDICOMFileLoader = null;
    private LISAImageGray16Bit mImage = null;               // The LISA 16-Bit image
    private boolean mIsInitialized = false;                 //Set if the DICOM Viewer is initialized or not

    private OnFragmentInteractionListener mListener;

    public DICOMFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DICOMFragment.
     */
    public static DICOMFragment newInstance(String fileName) {
        DICOMFragment fragment = new DICOMFragment();
        Bundle args = new Bundle();
        args.putString(FILE_NAME, fileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileName = getArguments().getString(FILE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dicom, container, false);

        mDICOMViewerData = new DICOMViewerData();
        mDICOMViewerData.setToolMode(ToolMode.DIMENSION);

        mImageView = (DICOMImageView) view.findViewById(R.id.image_view);
        mGrayscaleWindow = (GrayscaleWindowView) view.findViewById(R.id.grayscale_view);

        mImageView.setDICOMViewerData(mDICOMViewerData);
        mGrayscaleWindow.setDICOMViewerData(mDICOMViewerData);
        // recover file name if any
        if (savedInstanceState != null) {
            String fileName = null;
            fileName = savedInstanceState.getString(FILE_NAME);
            if (fileName != null)
                mFileName = fileName;
        }
        // load the file
        if (mFileName != null) {
            File currentFile = new File(mFileName);
            // Start the loading thread to load the DICOM image
            mDICOMFileLoader = new DICOMFileLoader(mLoadingHandler, currentFile);
            mDICOMFileLoader.start();
//            mBusy = true;
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileName != null)
            outState.putString(FILE_NAME, mFileName);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
//            mBusy = false;
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                case ThreadState.FINISHED:
                    // Set the loaded image
                    if (message.obj instanceof LISAImageGray16Bit) {
                        setImage((LISAImageGray16Bit) message.obj);
                    }
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
         * @param dicomFileReader
         * @return metadata that was read from file or null in case there no metadata or error
         * occurred.
         */
        private DICOMMetaInformation readMetadata(DICOMImageReader dicomFileReader) {
            DICOMMetaInformation metaInformation = null;
            try {
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.PROGRESSION_UPDATE;
                if (dicomFileReader.hasMetaInformation() == true)
                    metaInformation = dicomFileReader.parseMetaInformation();
                message.obj = metaInformation;
                mHandler.sendMessage(message);
            } catch (IOException | DICOMException e) {
                e.printStackTrace();
            }
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
                readMetadata(dicomFileReader);
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
                readMetadata(dicomFileReader);
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
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.OUT_OF_MEMORY;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);
            } catch (Exception ex) {
                Message message = mHandler.obtainMessage();
                message.what = ThreadState.UNCATCHABLE_ERROR_OCCURRED;
                message.obj = ex.getMessage();
                mHandler.sendMessage(message);

            }

        }

    }



}
