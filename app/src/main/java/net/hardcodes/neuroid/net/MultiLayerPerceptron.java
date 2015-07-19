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

package net.hardcodes.neuroid.net;


import net.hardcodes.neuroid.core.Layer;
import net.hardcodes.neuroid.core.NeuralNetwork;
import net.hardcodes.neuroid.core.input.WeightedSum;
import net.hardcodes.neuroid.core.transfer.Linear;
import net.hardcodes.neuroid.net.comp.neuron.BiasNeuron;
import net.hardcodes.neuroid.net.comp.neuron.InputNeuron;
import net.hardcodes.neuroid.net.learning.BackPropagation;
import net.hardcodes.neuroid.net.learning.MomentumBackpropagation;
import net.hardcodes.neuroid.util.ConnectionFactory;
import net.hardcodes.neuroid.util.LayerFactory;
import net.hardcodes.neuroid.util.NeuralNetworkFactory;
import net.hardcodes.neuroid.util.NeuralNetworkType;
import net.hardcodes.neuroid.util.NeuronProperties;
import net.hardcodes.neuroid.util.TransferFunctionType;
import net.hardcodes.neuroid.util.random.RangeRandomizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi Layer Perceptron neural network with Back propagation learning algorithm.
 *
 * @author Zoran Sevarac <sevarac@gmail.com>
 * @see net.hardcodes.neuroid.net.learning.BackPropagation
 * @see net.hardcodes.neuroid.net.learning.MomentumBackpropagation
 */
public class MultiLayerPerceptron extends NeuralNetwork<BackPropagation> {

    /**
     * The class fingerprint that is set to indicate serialization
     * compatibility with a previous version of the class.
     */
    private static final long serialVersionUID = 2L;

    /**
     * Creates new MultiLayerPerceptron with specified number of neurons in layers
     *
     * @param neuronsInLayers collection of neuron number in layers
     */
    public MultiLayerPerceptron(List<Integer> neuronsInLayers) {
        // init neuron settings
        NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", true);
        neuronProperties.setProperty("transferFunction", TransferFunctionType.SIGMOID);

        this.createNetwork(neuronsInLayers, neuronProperties);
    }

    public MultiLayerPerceptron(int... neuronsInLayers) {
        // init neuron settings
        NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", true);
        neuronProperties.setProperty("transferFunction", TransferFunctionType.SIGMOID);
        neuronProperties.setProperty("inputFunction", WeightedSum.class);

        List<Integer> neuronsInLayersVector = new ArrayList<>();
        for (int i = 0; i < neuronsInLayers.length; i++) {
            neuronsInLayersVector.add(new Integer(neuronsInLayers[i]));
        }

        this.createNetwork(neuronsInLayersVector, neuronProperties);
    }

    public MultiLayerPerceptron(TransferFunctionType transferFunctionType, int... neuronsInLayers) {
        // init neuron settings
        NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", true);
        neuronProperties.setProperty("transferFunction", transferFunctionType);
        neuronProperties.setProperty("inputFunction", WeightedSum.class);


        List<Integer> neuronsInLayersVector = new ArrayList<>();
        for (int i = 0; i < neuronsInLayers.length; i++) {
            neuronsInLayersVector.add(new Integer(neuronsInLayers[i]));
        }

        this.createNetwork(neuronsInLayersVector, neuronProperties);
    }

    public MultiLayerPerceptron(List<Integer> neuronsInLayers, TransferFunctionType transferFunctionType) {
        // init neuron settings
        NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", true);
        neuronProperties.setProperty("transferFunction", transferFunctionType);

        this.createNetwork(neuronsInLayers, neuronProperties);
    }

    /**
     * Creates new MultiLayerPerceptron net with specified number neurons in
     * getLayersIterator
     *
     * @param neuronsInLayers  collection of neuron numbers in layers
     * @param neuronProperties neuron properties
     */
    public MultiLayerPerceptron(List<Integer> neuronsInLayers, NeuronProperties neuronProperties) {
        this.createNetwork(neuronsInLayers, neuronProperties);
    }

    /**
     * Creates MultiLayerPerceptron Network architecture - fully connected
     * feed forward with specified number of neurons in each layer
     *
     * @param neuronsInLayers  collection of neuron numbers in getLayersIterator
     * @param neuronProperties neuron properties
     */
    private void createNetwork(List<Integer> neuronsInLayers, NeuronProperties neuronProperties) {

        // set network type
        this.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

        // create input layer
        NeuronProperties inputNeuronProperties = new NeuronProperties(InputNeuron.class, Linear.class);
        Layer layer = LayerFactory.createLayer(neuronsInLayers.get(0), inputNeuronProperties);

        boolean useBias = true; // use bias neurons by default
        if (neuronProperties.hasProperty("useBias")) {
            useBias = (Boolean) neuronProperties.getProperty("useBias");
        }

        if (useBias) {
            layer.addNeuron(new BiasNeuron());
        }

        this.addLayer(layer);

        // create layers
        Layer prevLayer = layer;

        //for(Integer neuronsNum : neuronsInLayers)
        for (int layerIdx = 1; layerIdx < neuronsInLayers.size(); layerIdx++) {
            Integer neuronsNum = neuronsInLayers.get(layerIdx);
            // createLayer layer
            layer = LayerFactory.createLayer(neuronsNum, neuronProperties);

            if (useBias && (layerIdx < (neuronsInLayers.size() - 1))) {
                layer.addNeuron(new BiasNeuron());
            }

            // add created layer to network
            this.addLayer(layer);
            // createLayer full connectivity between previous and this layer
            if (prevLayer != null) {
                ConnectionFactory.fullConnect(prevLayer, layer);
            }

            prevLayer = layer;
        }

        // set input and output cells for network
        NeuralNetworkFactory.setDefaultIO(this);

        // set learnng rule
//        this.setLearningRule(new BackPropagation());
        this.setLearningRule(new MomentumBackpropagation());
        // this.setLearningRule(new DynamicBackPropagation());

        this.randomizeWeights(new RangeRandomizer(-0.7, 0.7));

    }

    public void connectInputsToOutputs() {
        // connect first and last layer
        ConnectionFactory.fullConnect(getLayerAt(0), getLayerAt(getLayersCount() - 1), false);
    }

}