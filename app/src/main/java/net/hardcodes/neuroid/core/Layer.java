/**
 * Copyright 2014 Neuroph Project http://neuroph.sourceforge.net
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.hardcodes.neuroid.core;

import net.hardcodes.neuroid.core.events.NeuralNetworkEvent;
import net.hardcodes.neuroid.core.events.NeuralNetworkEventType;
import net.hardcodes.neuroid.util.NeuronFactory;
import net.hardcodes.neuroid.util.NeuronProperties;
import net.hardcodes.neuroid.util.NeurophArrayList;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

/**
 * <pre>
 * Layer of neurons in a neural network. The Layer is basic neuron container (a collection of neurons),
 * and it provides methods for manipulating neurons (add, remove, get, set, calculate, ...).
 * </pre>
 *
 * @see Neuron
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class Layer implements Serializable {

    private final String UID = UUID.randomUUID().toString();

    public String getUID() {
        return UID;
    }

    /**
     * The class fingerprint that is set to indicate serialization compatibility
     * with a previous version of the class
     */
    private static final long serialVersionUID = 4L;

    /**
     * Parent neural network - to which this layer belongs
     */
    private String parentNetworkUID;

    /**
     * Collection of neurons (Neuron instances)
     */
    protected NeurophArrayList<String> neuronUIDs;

    /**
     * Label for this layer
     */
    private String label;

    /**
     * Creates an instance of empty Layer
     */
    public Layer() {
        neuronUIDs = new NeurophArrayList(String.class);
    }

    /**
     * Creates an instance of empty Layer for specified number of neurons
     * @param neuronsCount number of neurons in this layer
     */
    public Layer(int neuronsCount) {
        neuronUIDs = new NeurophArrayList(String.class, neuronsCount);
    }

    /**
     * Creates an instance of Layer with the specified number of neurons with
     * specified neuron properties
     *
     * @param neuronsCount number of neurons in layer
     * @param neuronProperties properties of neurons in layer
     */
    public Layer(int neuronsCount, NeuronProperties neuronProperties) {
        neuronUIDs = new NeurophArrayList(String.class, neuronsCount);

        for (int i = 0; i < neuronsCount; i++) {
            Neuron neuron = NeuronFactory.createNeuron(neuronProperties);
            this.addNeuron(neuron);
        }
    }

    /**
     * Sets reference on parent network
     *
     * @param parent parent network
     */
    public final void setParentNetwork(NeuralNetwork parent) {
        this.parentNetworkUID = parent.getUID();
    }

    /**
     * Returns array neurons in this layer as array
     *
     * @return array of neurons in this layer
     */
    public final Neuron[] getNeurons() {
        Neuron[] neurons = new Neuron[neuronUIDs.size()];
        int i = 0;
        for (String neuronUID : neuronUIDs.asArray()) {
            neurons[i] = NeuralNetwork.NEURON_REGISTER.get(neuronUID);
            i++;
        }
        return neurons;
    }

    /**
     * Adds specified neuron to this layer
     *
     * @param neuron neuron to add
     */
    public final void addNeuron(Neuron neuron) {
        // prevent adding null neurons
        if (neuron == null) {
            throw new IllegalArgumentException("Neuron cant be null!");
        }

        // set neuron's parent layer to this layer 
        neuron.setParentLayer(this);

        // add new neuron at the end of the array
        neuronUIDs.add(neuron.getUID());
        NeuralNetwork.NEURON_REGISTER.put(neuron.getUID(), neuron);

        // notify network listeners that neuron has been added
        if (parentNetworkUID != null)
            NeuralNetwork.fireNetworkEvent(parentNetworkUID, new NeuralNetworkEvent(this, NeuralNetworkEventType.NEURON_ADDED));
    }

    /**
     * Adds specified neuron to this layer,at specified index position
     *
     * Throws IllegalArgumentException if neuron is null, or index is
     * illegal value (index<0 or index>neuronsCount)      
     *
     * @param neuron neuron to add
     * @param index index position at which neuron should be added
     */
    public final void addNeuron(int index, Neuron neuron) {
        // prevent adding null neurons
        if (neuron == null) {
            throw new IllegalArgumentException("Neuron cant be null!");
        }

        // add neuron to this layer
        neuronUIDs.add(index, neuron.getUID());
        NeuralNetwork.NEURON_REGISTER.put(neuron.getUID(), neuron);


        // set neuron's parent layer to this layer
        neuron.setParentLayer(this);

        // notify network listeners that neuron has been added
        if (parentNetworkUID != null)
            NeuralNetwork.fireNetworkEvent(parentNetworkUID, new NeuralNetworkEvent(this, NeuralNetworkEventType.NEURON_ADDED));
    }

    /**
     * Sets (replace) the neuron at specified position in layer
     *
     * @param index index position to set/replace
     * @param neuron new Neuron object to set
     */
    public final void setNeuron(int index, Neuron neuron) {
        // make sure that neuron is not null
        if (neuron == null) {
            throw new IllegalArgumentException("Neuron cant be null!");
        }

        // new neuron at specified index position
        NeuralNetwork.NEURON_REGISTER.remove(neuronUIDs.get(index));
        neuronUIDs.set(index, neuron.getUID());
        NeuralNetwork.NEURON_REGISTER.put(neuron.getUID(), neuron);

        // set neuron's parent layer to this layer                        
        neuron.setParentLayer(this);

        // notify network listeners that neuron has been added
        if (parentNetworkUID != null)
            NeuralNetwork.fireNetworkEvent(parentNetworkUID, new NeuralNetworkEvent(this, NeuralNetworkEventType.NEURON_ADDED));

    }

    /**
     * Removes neuron from layer
     *
     * @param neuron neuron to remove
     */
    public final void removeNeuron(Neuron neuron) {
        int index = indexOf(neuron);
        removeNeuronAt(index);
    }

    /**
     * Removes neuron at specified index position in this layer
     *
     * @param index index position of neuron to remove
     */
    public final void removeNeuronAt(int index) {
        Neuron neuron = NeuralNetwork.NEURON_REGISTER.get(neuronUIDs.get(index));
        neuron.setParentLayer(null);
        neuron.removeAllConnections(); // why we're doing this here? maybe we shouldnt
        NeuralNetwork.NEURON_REGISTER.remove(neuronUIDs.get(index));
        neuronUIDs.remove(index);

        // notify listeners that neuron has been removed
        if (parentNetworkUID != null)
            NeuralNetwork.fireNetworkEvent(parentNetworkUID, new NeuralNetworkEvent(this, NeuralNetworkEventType.NEURON_REMOVED));
    }

    public final void removeAllNeurons() {
        for (String neuronUID: neuronUIDs.asArray()) {
            NeuralNetwork.NEURON_REGISTER.remove(neuronUID);
        }
        neuronUIDs.clear();

        // notify listeners that neurons has been removed
        if (parentNetworkUID != null)
            NeuralNetwork.fireNetworkEvent(parentNetworkUID, new NeuralNetworkEvent(this, NeuralNetworkEventType.NEURON_REMOVED));
    }

    /**
     * Returns neuron at specified index position in this layer
     *
     * @param index neuron index position
     * @return neuron at specified index position
     */
    public Neuron getNeuronAt(int index) {
        return  NeuralNetwork.NEURON_REGISTER.get(neuronUIDs.get(index));
    }

    /**
     * Returns the index position in layer for the specified neuron
     *
     * @param neuron neuron object
     * @return index position of specified neuron
     */
    public int indexOf(Neuron neuron) {
        return neuronUIDs.indexOf(neuron.getUID());
    }

    /**
     * Returns number of neurons in this layer
     *
     * @return number of neurons in this layer
     */
    public int getNeuronsCount() {
        return neuronUIDs.size();
    }

    static final ForkJoinPool mainPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    /**
     * Performs calculaton for all neurons in this layer
     */
    public void calculate() {

        for (String neuronUID : this.neuronUIDs.asArray()) { // use directly underlying array since its faster
            NeuralNetwork.NEURON_REGISTER.get(neuronUID).calculate();
        }

//        mainPool.invokeAll(Arrays.asList(neurons.asArray()));
    }

    /**
     * Resets the activation and input levels for all neurons in this layer
     */
    public void reset() {
        for (String neuronUID : this.neuronUIDs.asArray()) { // use directly underlying array since its faster
            NeuralNetwork.NEURON_REGISTER.get(neuronUID).reset();
        }
    }

    /**
     * Initialize connection weights for the whole layer to to specified value
     *
     * @param value the weight value
     */
    public void initializeWeights(double value) {
        for (String neuronUID : this.neuronUIDs.asArray()) { // use directly underlying array since its faster
            NeuralNetwork.NEURON_REGISTER.get(neuronUID).initializeWeights(value);
        }
    }

    /**
     * Get layer label
     *
     * @return layer label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set layer label
     *
     * @param label layer label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEmpty() {
        return neuronUIDs.isEmpty();
    }

}