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
import net.hardcodes.neuroid.core.Neuron;
import net.hardcodes.neuroid.core.input.Difference;
import net.hardcodes.neuroid.core.transfer.Linear;
import net.hardcodes.neuroid.net.learning.KohonenLearning;
import net.hardcodes.neuroid.util.ConnectionFactory;
import net.hardcodes.neuroid.util.LayerFactory;
import net.hardcodes.neuroid.util.NeuralNetworkFactory;
import net.hardcodes.neuroid.util.NeuralNetworkType;
import net.hardcodes.neuroid.util.NeuronProperties;

/**
 * Kohonen neural network.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Kohonen extends NeuralNetwork {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new Kohonen network with specified number of neurons in input and
	 * map layer
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 * @param outputNeuronsCount
	 *            number of neurons in output layer
	 */
	public Kohonen(int inputNeuronsCount, int outputNeuronsCount) {
		this.createNetwork(inputNeuronsCount, outputNeuronsCount);
	}

	/**
	 * Creates Kohonen network architecture with specified number of neurons in
	 * input and map layer
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 * @param outputNeuronsCount
	 *            number of neurons in output layer
	 */
	private void createNetwork(int inputNeuronsCount, int outputNeuronsCount) {

		// specify input neuron properties (use default: weighted sum input with
		// linear transfer)
		NeuronProperties inputNeuronProperties = new NeuronProperties();

		// specify map neuron properties
		NeuronProperties outputNeuronProperties = new NeuronProperties(
                                            Neuron.class,        // neuron type
                                            Difference.class,   // input function
                                            Linear.class       // transfer function
                                                    );
		// set network type
		this.setNetworkType(NeuralNetworkType.KOHONEN);

		// createLayer input layer
		Layer inLayer = LayerFactory.createLayer(inputNeuronsCount,
				inputNeuronProperties);
		this.addLayer(inLayer);

		// createLayer map layer
		Layer mapLayer = LayerFactory.createLayer(outputNeuronsCount,
				outputNeuronProperties);
		this.addLayer(mapLayer);

		// createLayer full connectivity between input and output layer
		ConnectionFactory.fullConnect(inLayer, mapLayer);

		// set network input and output cells
		NeuralNetworkFactory.setDefaultIO(this);

		this.setLearningRule(new KohonenLearning());
	}

}
