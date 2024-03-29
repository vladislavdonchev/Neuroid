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
import net.hardcodes.neuroid.net.comp.neuron.ThresholdNeuron;
import net.hardcodes.neuroid.net.learning.BinaryDeltaRule;
import net.hardcodes.neuroid.util.ConnectionFactory;
import net.hardcodes.neuroid.util.LayerFactory;
import net.hardcodes.neuroid.util.NeuralNetworkFactory;
import net.hardcodes.neuroid.util.NeuralNetworkType;
import net.hardcodes.neuroid.util.NeuronProperties;
import net.hardcodes.neuroid.util.TransferFunctionType;

/**
 * Perceptron neural network with some LMS based learning algorithm.
 *
 * @see net.hardcodes.neuroid.net.learning.PerceptronLearning
 * @see net.hardcodes.neuroid.net.learning.BinaryDeltaRule
 * @see net.hardcodes.neuroid.net.learning.SigmoidDeltaRule
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Perceptron extends NeuralNetwork {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new Perceptron with specified number of neurons in input and
	 * output layer, with Step trqansfer function
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 * @param outputNeuronsCount
	 *            number of neurons in output layer
	 */
	public Perceptron(int inputNeuronsCount, int outputNeuronsCount) {
		this.createNetwork(inputNeuronsCount, outputNeuronsCount, TransferFunctionType.STEP);
	}

	/**
	 * Creates new Perceptron with specified number of neurons in input and
	 * output layer, and specified transfer function
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 * @param outputNeuronsCount
	 *            number of neurons in output layer
	 * @param transferFunctionType
	 *            transfer function type
	 */
	public Perceptron(int inputNeuronsCount, int outputNeuronsCount, TransferFunctionType transferFunctionType) {
		this.createNetwork(inputNeuronsCount, outputNeuronsCount, transferFunctionType);
	}

	/**
	 * Creates perceptron architecture with specified number of neurons in input
	 * and output layer, specified transfer function
	 * 
	 * @param inputNeuronsCount
	 *            number of neurons in input layer
	 * @param outputNeuronsCount
	 *            number of neurons in output layer
	 * @param transferFunctionType
	 *            neuron transfer function type
	 */
	private void createNetwork(int inputNeuronsCount, int outputNeuronsCount, TransferFunctionType transferFunctionType) {
		// set network type
		this.setNetworkType(NeuralNetworkType.PERCEPTRON);

		// init neuron settings for input layer
		NeuronProperties inputNeuronProperties = new NeuronProperties();
   		inputNeuronProperties.setProperty("transferFunction", TransferFunctionType.LINEAR);

		// create input layer
		Layer inputLayer = LayerFactory.createLayer(inputNeuronsCount, inputNeuronProperties);
		this.addLayer(inputLayer);

		NeuronProperties outputNeuronProperties = new NeuronProperties();
		outputNeuronProperties.setProperty("neuronType", ThresholdNeuron.class);
		outputNeuronProperties.setProperty("thresh", new Double(Math.abs(Math.random())));
		outputNeuronProperties.setProperty("transferFunction", transferFunctionType);
		// for sigmoid and tanh transfer functions set slope propery
		outputNeuronProperties.setProperty("transferFunction.slope", new Double(1));

		// createLayer output layer
		Layer outputLayer = LayerFactory.createLayer(outputNeuronsCount, outputNeuronProperties);
		this.addLayer(outputLayer);

		// create full conectivity between input and output layer
		ConnectionFactory.fullConnect(inputLayer, outputLayer);

		// set input and output cells for this network
		NeuralNetworkFactory.setDefaultIO(this);
                
                this.setLearningRule(new BinaryDeltaRule());
		// set appropriate learning rule for this network
//		if (transferFunctionType == TransferFunctionType.STEP) {
//			this.setLearningRule(new BinaryDeltaRule(this));
//		} else if (transferFunctionType == TransferFunctionType.SIGMOID) {
//			this.setLearningRule(new SigmoidDeltaRule(this));
//		} else if (transferFunctionType == TransferFunctionType.TANH) {
//			this.setLearningRule(new SigmoidDeltaRule(this));
//		} else {
//			this.setLearningRule(new PerceptronLearning(this));
//		}
	}

}
