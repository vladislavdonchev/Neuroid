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

package net.hardcodes.neuroid.net.comp.neuron;

import net.hardcodes.neuroid.core.Connection;
import net.hardcodes.neuroid.core.input.InputFunction;
import net.hardcodes.neuroid.core.transfer.TransferFunction;

import java.util.Arrays;

/**
 * Provides neuron behaviour specific for competitive neurons which are used in
 * competitive layers, and networks with competitive learning.
 * 
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class CompetitiveNeuron extends DelayedNeuron {
	
	/**
	 * The class fingerprint that is set to indicate serialization
	 * compatibility with a previous version of the class.
	 */		
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag indicates if this neuron is in competing mode
	 */
	private boolean isCompeting = false;
	
	/**
	 * Collection of conections from neurons in other layers
	 */
	private Connection[] connectionsFromOtherLayers;
	
	/**
	 * Collection of connections from neurons in the same layer as this neuron
	 * (lateral connections used for competition)
	 */
	private Connection[] connectionsFromThisLayer;

	/**
	 * Creates an instance of CompetitiveNeuron with specified input and transfer functions
	 * @param inputFunction neuron input function
	 * @param transferFunction neuron ransfer function
	 */
	public CompetitiveNeuron(InputFunction inputFunction, TransferFunction transferFunction) {
		super(inputFunction, transferFunction);
		connectionsFromOtherLayers = new Connection[0];
		connectionsFromThisLayer = new Connection[0];
		addInputConnection(this, 1);
	}

	@Override
	public void calculate() {
		if (this.isCompeting) {
			// get input only from neurons in this layer
			this.netInput = this.inputFunction
					.getOutput(this.connectionsFromThisLayer);
		} else {
			// get input from other layers
			this.netInput = this.inputFunction
					.getOutput(this.connectionsFromOtherLayers);
			this.isCompeting = true;
		}

		this.output = this.transferFunction.getOutput(this.netInput);
		outputHistory.add(0, new Double(this.output));
	}

	/**
	 * Adds input connection for this competitive neuron
	 * @param connection input connection
	 */
	@Override
	public void addInputConnection(Connection connection) {
		super.addInputConnection(connection);
		if (connection.getFromNeuron().getParentLayer() == this
				.getParentLayer()) {
                    this.connectionsFromThisLayer =  Arrays.copyOf(connectionsFromThisLayer, connectionsFromThisLayer.length+1);     // grow existing connections  array to make space for new connection
                    this.connectionsFromThisLayer[connectionsFromThisLayer.length - 1] = connection;                    
		//	connectionsFromThisLayer.add(connection);
		} else {
                    this.connectionsFromOtherLayers =  Arrays.copyOf(connectionsFromOtherLayers, connectionsFromOtherLayers.length+1);     // grow existing connections  array to make space for new connection
                    this.connectionsFromOtherLayers[connectionsFromOtherLayers.length - 1] = connection;                          
		//	connectionsFromOtherLayers.add(connection);
		}
	}

	/**
	 * Returns collection of connections from other layers
	 * @return collection of connections from other layers
	 */
	public Connection[] getConnectionsFromOtherLayers() {
		return connectionsFromOtherLayers;
	}

	/**
	 * Resets the input, output and mode for this neuron
	 */
	@Override
	public void reset() {
		super.reset();
		this.isCompeting = false;
	}

	/**
	 * Retruns true if this neuron is in competing mode, false otherwise
	 * @return true if this neuron is in competing mode, false otherwise
	 */
	public boolean isCompeting() {
		return isCompeting;
	}

	/**
	 * Sets the flag to indicate that this neuron is in competing mode
	 * @param isCompeting value for the isCompeting flag
	 */
	public void setIsCompeting(boolean isCompeting) {
		this.isCompeting = isCompeting;
	}

}
