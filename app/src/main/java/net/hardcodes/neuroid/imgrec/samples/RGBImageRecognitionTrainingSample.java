/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hardcodes.neuroid.imgrec.samples;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import net.hardcodes.neuroid.core.NeuralNetwork;
import net.hardcodes.neuroid.core.data.DataSet;
import net.hardcodes.neuroid.imgrec.ColorMode;
import net.hardcodes.neuroid.imgrec.FractionRgbData;
import net.hardcodes.neuroid.imgrec.ImageRecognitionHelper;
import net.hardcodes.neuroid.imgrec.image.Dimension;
import net.hardcodes.neuroid.net.learning.MomentumBackpropagation;
import net.hardcodes.neuroid.util.TransferFunctionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Zoran Sevarac
 */
public class RGBImageRecognitionTrainingSample {
    
    public static NeuralNetwork train(Context context) throws IOException {
        
        // path to image directory
        String imageDir = Environment.getExternalStorageDirectory() + "/ir/trening/";
        
        // image names - used for output neuron labels
        List<String> imageLabels = new ArrayList();
        imageLabels.add("bird");                                                   
        imageLabels.add("cat");
        imageLabels.add("horse");
                

        // create dataset
        Map<String,FractionRgbData> map = ImageRecognitionHelper.getFractionRgbDataForDirectory(new File(imageDir), new Dimension(64, 64));
        DataSet dataSet = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, map);

        // create neural network
        List <Integer> hiddenLayers = new ArrayList<>();
        hiddenLayers.add(12);
        NeuralNetwork nnet = ImageRecognitionHelper.createNewNeuralNetwork("someNetworkName", new Dimension(64,64), ColorMode.COLOR_RGB, imageLabels, hiddenLayers, TransferFunctionType.SIGMOID);

        System.out.println("NNet start learning...");
        // set learning rule parameters
        MomentumBackpropagation mb = (MomentumBackpropagation)nnet.getLearningRule();
        mb.setLearningRate(0.2);
        mb.setMaxError(0.9);
        mb.setMomentum(1);
      
        // traiin network
        nnet.learn(dataSet);
        System.out.println("NNet learned");                

        return nnet;
    }
    
}
