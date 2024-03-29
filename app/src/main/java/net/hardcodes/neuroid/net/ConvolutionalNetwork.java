/**
 * Copyright 2013 Neuroph Project http://neuroph.sourceforge.net
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
import net.hardcodes.neuroid.core.Neuron;
import net.hardcodes.neuroid.core.exceptions.VectorSizeMismatchException;
import net.hardcodes.neuroid.core.input.WeightedSum;
import net.hardcodes.neuroid.net.comp.ConvolutionalUtils;
import net.hardcodes.neuroid.net.comp.Kernel;
import net.hardcodes.neuroid.net.comp.layer.ConvolutionalLayer;
import net.hardcodes.neuroid.net.comp.layer.FeatureMapsLayer;
import net.hardcodes.neuroid.net.comp.layer.InputMapsLayer;
import net.hardcodes.neuroid.net.comp.layer.Layer2D;
import net.hardcodes.neuroid.net.comp.layer.PoolingLayer;
import net.hardcodes.neuroid.net.comp.neuron.BiasNeuron;
import net.hardcodes.neuroid.net.learning.BackPropagation;
import net.hardcodes.neuroid.net.learning.ConvolutionalBackpropagation;
import net.hardcodes.neuroid.util.ConnectionFactory;
import net.hardcodes.neuroid.util.NeuronProperties;
import net.hardcodes.neuroid.util.TransferFunctionType;

/**
 * Convolutional neural network with backpropagation algorithm modified for
 * convolutional networks.
 * <p/>
 * TODO: provide Hiton, LeCun, AndrewNg implementation specific features
 *
 * @author Boris Fulurija
 * @author Zoran Sevarac
 * @see ConvolutionalBackpropagation
 */
public class ConvolutionalNetwork extends NeuralNetwork<BackPropagation> {

    private static final long serialVersionUID = -1393907449047650509L;


    public ConvolutionalNetwork() {

    }

    /**
     * Sets network input, to all feature maps in input layer
     *
     * @param inputVector
     * @throws VectorSizeMismatchException
     */
    @Override
    public void setInput(double... inputVector) throws VectorSizeMismatchException {
        FeatureMapsLayer inputLayer = (FeatureMapsLayer) getLayerAt(0);
        int currentNeuron = 0;
        for (int i = 0; i < inputLayer.getNumberOfMaps(); i++) {
            Layer2D map = inputLayer.getFeatureMap(i);
            for (Neuron neuron : map.getNeurons()) {
                if (!(neuron instanceof BiasNeuron))
                    neuron.setInput(inputVector[currentNeuron++]);
            }
        }
    }

    public static class ConvolutionalNetworkBuilder {

        public static final NeuronProperties DEFAULT_FULL_CONNECTED_NEURON_PROPERTIES = new NeuronProperties();
        private ConvolutionalNetwork network;

        static {
            DEFAULT_FULL_CONNECTED_NEURON_PROPERTIES.setProperty("useBias", true);
            DEFAULT_FULL_CONNECTED_NEURON_PROPERTIES.setProperty("transferFunction", TransferFunctionType.SIGMOID);
            DEFAULT_FULL_CONNECTED_NEURON_PROPERTIES.setProperty("inputFunction", WeightedSum.class);
        }

        public ConvolutionalNetworkBuilder(Layer2D.Dimensions mapSize, int numberOfMaps) {
            network = new ConvolutionalNetwork();
            InputMapsLayer inputLayer = new InputMapsLayer(mapSize, numberOfMaps);
            inputLayer.setLabel("Input Layer");
            network.addLayer(inputLayer);

        }

        public ConvolutionalNetworkBuilder withConvolutionLayer(final Kernel convolutionKernel, int numberOfMaps) {
            FeatureMapsLayer lastLayer = getLastFeatureMapLayer();
            ConvolutionalLayer convolutionLayer = new ConvolutionalLayer(lastLayer, convolutionKernel, numberOfMaps);

            network.addLayer(convolutionLayer);
            ConvolutionalUtils.fullConnectMapLayers(lastLayer, convolutionLayer);

            return this;
        }

        public ConvolutionalNetworkBuilder withPoolingLayer(final Kernel poolingKernel) {
            FeatureMapsLayer lastLayer = getLastFeatureMapLayer();
            PoolingLayer poolingLayer = new PoolingLayer(lastLayer, poolingKernel);

            network.addLayer(poolingLayer);
            ConvolutionalUtils.fullConnectMapLayers(lastLayer, poolingLayer);

            return this;
        }

        public ConvolutionalNetworkBuilder withFullConnectedLayer(int numberOfNeurons) {
            Layer lastLayer = getLastLayer();

            Layer fullConnectedLayer = new Layer(numberOfNeurons, DEFAULT_FULL_CONNECTED_NEURON_PROPERTIES);
            network.addLayer(fullConnectedLayer);


            ConnectionFactory.fullConnect(lastLayer, fullConnectedLayer);

            return this;
        }

        public ConvolutionalNetwork createNetwork() {
            network.setInputNeurons(network.getLayerAt(0).getNeurons());
            network.setOutputNeurons(getLastLayer().getNeurons());
            return network;
        }


        private FeatureMapsLayer getLastFeatureMapLayer() {
            Layer layer = getLastLayer();
            if (layer instanceof FeatureMapsLayer)
                return (FeatureMapsLayer) layer;

            throw new RuntimeException("Unable to add next layer because previous layer is not FeatureMapLayer");
        }

        private Layer getLastLayer() {
            return network.getLayerAt(network.getLayersCount() - 1);
        }


    }


}