package net.hardcodes.neuroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.hardcodes.neuroid.R;
import net.hardcodes.neuroid.core.NeuralNetwork;
import net.hardcodes.neuroid.core.data.DataSet;
import net.hardcodes.neuroid.core.events.LearningEvent;
import net.hardcodes.neuroid.core.events.LearningEventListener;
import net.hardcodes.neuroid.core.events.LearningEventType;
import net.hardcodes.neuroid.core.learning.LearningRule;
import net.hardcodes.neuroid.imgrec.ColorMode;
import net.hardcodes.neuroid.imgrec.FractionRgbData;
import net.hardcodes.neuroid.imgrec.ImageRecognitionHelper;
import net.hardcodes.neuroid.imgrec.ImageRecognitionPlugin;
import net.hardcodes.neuroid.imgrec.image.Dimension;
import net.hardcodes.neuroid.imgrec.image.Image;
import net.hardcodes.neuroid.imgrec.image.ImageAndroid;
import net.hardcodes.neuroid.net.learning.AntiHebbianLearning;
import net.hardcodes.neuroid.net.learning.MomentumBackpropagation;
import net.hardcodes.neuroid.util.TransferFunctionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MnQko on 19.7.2015 г..
 */
public class ImageRecognitionManager implements View.OnClickListener, LearningEventListener {

    private NeuralNetwork neuralNetwork;

    private View contents;

    private ProgressDialog progressDialog;
    private FileDialog fileDialog;
    private File path;

    private EditText nnNameText;
    private EditText hiddenLayersText;
    private EditText sampleWidthText;
    private EditText sampleHeightText;

    private Spinner colorModeSpinner;
    private Spinner learningRuleSpinner;
    private Spinner transferFunctionSpinner;

    private Button selectImageButton;
    private Button trainButton;
    private Button testButton;
    private Button saveButton;
    private Button loadButton;

    private ImageView selectedImageView;
    private TextView resultText;

    private HashMap<String, ColorMode> colorModeEntries = new HashMap<>();
    private HashMap<String, Class> learningRuleEntries = new HashMap<>();
    private HashMap<String, TransferFunctionType> transferFunctionEntries = new HashMap<>();

    public ImageRecognitionManager(LayoutInflater inflater, ViewGroup parent) {
        initContents(inflater, parent);
    }

    private void initContents(LayoutInflater inflater, ViewGroup parent) {
        contents = inflater.inflate(R.layout.fragment_image_recognition, parent, false);

        nnNameText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_name);
        hiddenLayersText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_hidden);
        sampleWidthText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_sample_width);
        sampleHeightText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_sample_height);

        colorModeSpinner = (Spinner) contents.findViewById(R.id.fragment_image_recognition_nn_color);
        learningRuleSpinner = (Spinner) contents.findViewById(R.id.fragment_image_recognition_nn_learning);
        transferFunctionSpinner = (Spinner) contents.findViewById(R.id.fragment_image_recognition_nn_transfer);
        initSpinners(contents);

        selectImageButton = (Button) contents.findViewById(R.id.fragment_image_recognition_test_image_select);
        trainButton = (Button) contents.findViewById(R.id.fragment_image_recognition_train_start);
        testButton = (Button) contents.findViewById(R.id.fragment_image_recognition_recognize_start);
        saveButton = (Button) contents.findViewById(R.id.fragment_image_recognition_nn_save);
        loadButton = (Button) contents.findViewById(R.id.fragment_image_recognition_nn_load);

        selectImageButton.setOnClickListener(this);
        trainButton.setOnClickListener(this);
        testButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);

        selectedImageView = (ImageView) contents.findViewById(R.id.fragment_image_recognition_test_image);
        resultText = (TextView) contents.findViewById(R.id.fragment_image_recognition_test_result);

        progressDialog = new ProgressDialog(contents.getContext());
        path = new File("/sdcard/ir/trening/");
        fileDialog = new FileDialog((Activity) contents.getContext(), path);
    }

    private void initSpinners(View parent) {
        colorModeEntries.put("RGB", ColorMode.COLOR_RGB);
        colorModeEntries.put("HSL", ColorMode.COLOR_HSL);
        colorModeEntries.put("B&W", ColorMode.BLACK_AND_WHITE);

        learningRuleEntries.put("Momentum Back-Propagation", MomentumBackpropagation.class);

        transferFunctionEntries.put("Linear", TransferFunctionType.LINEAR);
        transferFunctionEntries.put("Ramp", TransferFunctionType.RAMP);
        transferFunctionEntries.put("Step", TransferFunctionType.STEP);
        transferFunctionEntries.put("Sigmoid", TransferFunctionType.SIGMOID);
        transferFunctionEntries.put("Tanh", TransferFunctionType.TANH);
        transferFunctionEntries.put("Gaussian", TransferFunctionType.GAUSSIAN);
        transferFunctionEntries.put("Trapezoid", TransferFunctionType.TRAPEZOID);
        transferFunctionEntries.put("Sgn", TransferFunctionType.SGN);
        transferFunctionEntries.put("Sin", TransferFunctionType.SIN);
        transferFunctionEntries.put("Log", TransferFunctionType.LOG);

        colorModeSpinner.setAdapter(new ArrayAdapter<>(parent.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(colorModeEntries.keySet())));
        learningRuleSpinner.setAdapter(new ArrayAdapter<>(parent.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(learningRuleEntries.keySet())));
        transferFunctionSpinner.setAdapter(new ArrayAdapter<>(parent.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(transferFunctionEntries.keySet())));
    }

    @Override
    public void onClick(View v) {

        int w = Integer.parseInt(sampleWidthText.getText().toString());
        int h = Integer.parseInt(sampleHeightText.getText().toString());

        switch (v.getId()) {
            case R.id.fragment_image_recognition_test_image_select:
                fileDialog.setFileEndsWith(".jpg");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        path = file;
                        ImageAndroid imageAndroid = new ImageAndroid(path);
                        selectedImageView.setImageBitmap(imageAndroid.getBitmap());
                    }
                });
                //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                //  public void directorySelected(File directory) {
                //      Log.d(getClass().getName(), "selected dir " + directory.toString());
                //  }
                //});
                //fileDialog.setSelectDirectoryOption(false);
                fileDialog.showDialog();
                break;
            case R.id.fragment_image_recognition_train_start:
                // path to image directory
                String imageDir = Environment.getExternalStorageDirectory() + "/ir/trening/";

                // image names - used for output neuron labels
                List<String> imageLabels = new ArrayList();
                imageLabels.add("bird");
                imageLabels.add("cat");
                imageLabels.add("horse");

                // create dataset
                Map<String,FractionRgbData> map = null;
                try {
                    map = ImageRecognitionHelper.getFractionRgbDataForDirectory(new File(imageDir), new Dimension(w, h));
                } catch (IOException e) {
                }
                final DataSet dataSet = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, map);

                // create neural network
                List <Integer> hiddenLayers = new ArrayList<>();
                hiddenLayers.add(Integer.parseInt(hiddenLayersText.getText().toString()));
                neuralNetwork = ImageRecognitionHelper.createNewNeuralNetwork(nnNameText.getText().toString(),
                        new Dimension(w, h),
                        colorModeEntries.get(colorModeSpinner.getSelectedItem().toString()), imageLabels, hiddenLayers,
                        transferFunctionEntries.get(transferFunctionSpinner.getSelectedItem().toString()));

                // set learning rule parameters
                MomentumBackpropagation mb = (MomentumBackpropagation) neuralNetwork.getLearningRule();
                mb.setLearningRate(0.2);
                mb.setMaxError(0.9);
                mb.setMomentum(1);

                mb.addListener(this);

                progressDialog.show();
                neuralNetwork.learnInNewThread(dataSet);
                break;
            case R.id.fragment_image_recognition_recognize_start:
                if (neuralNetwork == null) {
                    Toast.makeText(contents.getContext(), "Please train the network first...", Toast.LENGTH_LONG).show();
                    return;
                }

                // get the image recognition plugin from neural network
                ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) neuralNetwork.getPlugin(ImageRecognitionPlugin.class);
                resultText.setText("Recognizing...");

                try {
                    // image recognition is done here
                    ImageAndroid imageAndroid = new ImageAndroid(path);
                    Image img = ImageAndroid.padSquare(imageAndroid);
                    img = img.resize(w, h);
                    HashMap<String, Double> output = imageRecognition.recognizeImage(img); // specify some existing image file here
                    System.out.println(output.toString());
                    resultText.setText(output.toString());
                } catch(Exception e) {
                    Log.d("Error: ", e.getMessage(), e);
                }
                break;
            case R.id.fragment_image_recognition_nn_save:

                break;
            case R.id.fragment_image_recognition_nn_load:

                break;
        }
    }

    public View getContents() {
        return contents;
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        if (event.getEventType().equals(LearningEventType.LEARNING_STOPPED)) {
            progressDialog.dismiss();
        }
    }
}
