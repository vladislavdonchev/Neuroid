package net.hardcodes.neuroid.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.hardcodes.neuroid.R;
import net.hardcodes.neuroid.core.NeuralNetwork;
import net.hardcodes.neuroid.core.data.DataSet;
import net.hardcodes.neuroid.core.events.LearningEvent;
import net.hardcodes.neuroid.core.events.LearningEventListener;
import net.hardcodes.neuroid.core.events.LearningEventType;
import net.hardcodes.neuroid.imgrec.ColorMode;
import net.hardcodes.neuroid.imgrec.FractionRgbData;
import net.hardcodes.neuroid.imgrec.ImageRecognitionHelper;
import net.hardcodes.neuroid.imgrec.ImageRecognitionPlugin;
import net.hardcodes.neuroid.imgrec.image.Dimension;
import net.hardcodes.neuroid.imgrec.image.ImageAndroid;
import net.hardcodes.neuroid.net.learning.MomentumBackpropagation;
import net.hardcodes.neuroid.util.TransferFunctionType;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MnQko on 19.7.2015 г..
 */
public class ImageRecognitionManager implements View.OnClickListener {

    private NeuralNetwork neuralNetwork;

    private static View contents;

    public static ProgressDialog progressDialog;
    private FileDialog imageRecognitionFileDialog;
    private FileDialog neuralNetworkFileDialog;
    private File path;
    private ImageAndroid image;

    private EditText nnNameText;
    private EditText hiddenLayersText;
    private EditText hiddenNeuronsText;
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

    private boolean learning = false;

    public ImageRecognitionManager(LayoutInflater inflater, ViewGroup parent) {
        initContents(inflater, parent);

        final int w = Integer.parseInt(sampleWidthText.getText().toString());
        final int h = Integer.parseInt(sampleHeightText.getText().toString());

        progressDialog = new ProgressDialog(contents.getContext());
        path = new File("/sdcard/ir/trening/");
        imageRecognitionFileDialog = new FileDialog((Activity) contents.getContext(), path);
        neuralNetworkFileDialog = new FileDialog((Activity) contents.getContext(), new File("/sdcard/"));

        imageRecognitionFileDialog.setFileEndsWith(".jpg");
        imageRecognitionFileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                path = file;
                image = ImageAndroid.padSquare(new ImageAndroid(path)).resize(w, h);
                selectedImageView.setImageBitmap(image.getBitmap());
            }
        });

        neuralNetworkFileDialog.setFileEndsWith(".nnn");
        neuralNetworkFileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                neuralNetwork = NeuralNetwork.createFromFile(file);
                Toast.makeText(contents.getContext(), "Neural network loaded!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initContents(LayoutInflater inflater, ViewGroup parent) {
        contents = inflater.inflate(R.layout.view_image_recognition, parent, false);

        nnNameText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_name);
        hiddenLayersText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_hidden);
        hiddenNeuronsText = (EditText) contents.findViewById(R.id.fragment_image_recognition_nn_neuron);
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

        transferFunctionSpinner.setSelection(6);
    }

    public void updateProgressDialog() {
        if (!learning && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        final int w = Integer.parseInt(sampleWidthText.getText().toString());
        final int h = Integer.parseInt(sampleHeightText.getText().toString());

        switch (v.getId()) {
            case R.id.fragment_image_recognition_test_image_select:
                imageRecognitionFileDialog.showDialog();
                break;
            case R.id.fragment_image_recognition_train_start:
                Dimension sampleSize = new Dimension(w, h);

                List<Integer> hiddenLayers = new ArrayList<>();
                for(int i = 0; i < Integer.parseInt(hiddenLayersText.getText().toString()); i++) {
                    hiddenLayers.add(Integer.parseInt(hiddenNeuronsText.getText().toString()));
                }

                new PrepareTrainingSetAndLearnTask(nnNameText.getText().toString(), colorModeEntries.get(colorModeSpinner.getSelectedItem().toString()),
                        transferFunctionEntries.get(transferFunctionSpinner.getSelectedItem().toString()), hiddenLayers)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sampleSize);
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
                    HashMap<String, Double> output = imageRecognition.recognizeImage(image); // specify some existing image file here
                    for (Map.Entry<String, Double> outputEntry: output.entrySet()) {
                        outputEntry.setValue(Double.parseDouble(new DecimalFormat("#.#####").format(outputEntry.getValue())));
                    }
                    System.out.println(output.toString());
                    resultText.setText(output.toString().replace("{", "").replace("}", "").replace(".jpg", "").replace("=", " = ").replace(",", "\n"));
                } catch (Exception e) {
                    Log.d("Error: ", e.getMessage(), e);
                }
                break;
            case R.id.fragment_image_recognition_nn_save:
                neuralNetwork.save("/sdcard/" + nnNameText.getText().toString() + ".nnn");
                Toast.makeText(contents.getContext(), "Neural network saved!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_image_recognition_nn_load:
                neuralNetworkFileDialog.showDialog();
                break;
        }
    }

    private class PrepareTrainingSetAndLearnTask extends AsyncTask<Dimension, String, Void> {

        private String networkName;
        private ColorMode colorMode;
        private TransferFunctionType transferFunctionType;
        private List<Integer> hiddenLayers;

        public PrepareTrainingSetAndLearnTask(String networkName, ColorMode colorMode, TransferFunctionType transferFunctionType, List<Integer> hiddenLayers) {
            this.networkName = networkName;
            this.colorMode = colorMode;
            this.transferFunctionType = transferFunctionType;
            this.hiddenLayers = hiddenLayers;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Building neural network...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            learning = true;
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Dimension... params) {// path to image directory

            File imageDir = new File(Environment.getExternalStorageDirectory() + "/ir/trening/");
            // image names - used for output neuron labels
            List<String> imageLabels = new ArrayList<>();

            for (String image: imageDir.list()) {
                imageLabels.add(image);
            }

            neuralNetwork = ImageRecognitionHelper.createNewNeuralNetwork(networkName,
                    params[0], colorMode, imageLabels, hiddenLayers, transferFunctionType);

            publishProgress("Preparing data set...");

            // create dataset
            Map<String, FractionRgbData> map = null;
            try {
                map = ImageRecognitionHelper.getFractionRgbDataForDirectory(imageDir, params[0]);
            } catch (IOException e) {
            }
            final DataSet dataSet = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, map);

            // set learning rule parameters
            MomentumBackpropagation mb = (MomentumBackpropagation) neuralNetwork.getLearningRule();
            mb.setLearningRate(0.2);
            mb.setMaxError(0.8);
            mb.setMomentum(1);

            publishProgress("Training network...");
            neuralNetwork.learn(dataSet);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            neuralNetwork.save("/sdcard/" + nnNameText.getText().toString() + "_autosave.nnn");
            Toast.makeText(contents.getContext(), "Neural network trained!", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();
            learning = false;
            super.onPostExecute(aVoid);
        }
    }

    public View getContents() {
        return contents;
    }

    private static class UpdateProgressDialogTextRunnable implements Runnable {

        private String text;

        public UpdateProgressDialogTextRunnable(String text) {
            this.text = text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            progressDialog.setMessage(text);
        }
    }

    private static UpdateProgressDialogTextRunnable updateProgressDialogTextRunnable = new UpdateProgressDialogTextRunnable("");

    public static void updateProgressDialogText(String text) {
        updateProgressDialogTextRunnable.setText(text);
        ((Activity) contents.getContext()).runOnUiThread(updateProgressDialogTextRunnable);
    }
}
