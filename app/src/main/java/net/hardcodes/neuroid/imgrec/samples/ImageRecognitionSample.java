/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hardcodes.neuroid.imgrec.samples;

import net.hardcodes.neuroid.core.NeuralNetwork;
import net.hardcodes.neuroid.core.exceptions.VectorSizeMismatchException;
import net.hardcodes.neuroid.imgrec.ImageRecognitionPlugin;
import net.hardcodes.neuroid.imgrec.ImageSampler;
import net.hardcodes.neuroid.imgrec.image.Dimension;
import net.hardcodes.neuroid.imgrec.image.Image;
import net.hardcodes.neuroid.imgrec.image.ImageAndroid;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This sample shows how to use the image recognition neural network in your applications.
 * IMPORTANT NOTE: specify filenames for neural network and test image, or you'll get IOException
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class ImageRecognitionSample {  

    public static String recognize(NeuralNetwork nnet) {
          // load trained neural network saved with easyNeurons (specify existing neural network file here)
        if (nnet == null) {
            nnet = NeuralNetwork.createFromFile("MyImageRecognition.nnet");
        }
          // get the image recognition plugin from neural network
          ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)nnet.getPlugin(ImageRecognitionPlugin.class);

          try {
                // image recognition is done here
                Image img = ImageAndroid.padSquare(new ImageAndroid(new File("/sdcard/ir/guess1.jpg")));
                img = img.resize(64, 64);
                HashMap<String, Double> output = imageRecognition.recognizeImage(img); // specify some existing image file here
                System.out.println(output.toString());
                return output.toString();
          } catch(Exception e) {
              System.out.println("Error: " + e.getMessage());
          }
        return "Fail :(";
    }
}
