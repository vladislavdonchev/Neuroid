/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.hardcodes.neuroid.util;

import net.hardcodes.neuroid.core.Neuron;
import net.hardcodes.neuroid.core.input.InputFunction;
import net.hardcodes.neuroid.core.input.WeightedSum;
import net.hardcodes.neuroid.core.transfer.Linear;
import net.hardcodes.neuroid.core.transfer.TransferFunction;

import java.util.Iterator;

/**
 * Represents properties of a neuron.
 *
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class NeuronProperties extends Properties {

    private static final long serialVersionUID = 2L;

    public NeuronProperties() {
        initKeys();
        this.setProperty("inputFunction", WeightedSum.class);
        this.setProperty("transferFunction", Linear.class);
        this.setProperty("neuronType", Neuron.class);
    }

    public NeuronProperties(Class<? extends Neuron> neuronClass) {
        initKeys();
        this.setProperty("inputFunction", WeightedSum.class);
        this.setProperty("transferFunction", Linear.class);
        this.setProperty("neuronType", neuronClass);
    }

    public NeuronProperties(Class<? extends Neuron> neuronClass, Class<? extends TransferFunction> transferFunctionClass) {
        initKeys();
        this.setProperty("inputFunction", WeightedSum.class);
        this.setProperty("transferFunction", transferFunctionClass);
        this.setProperty("neuronType", neuronClass);
    }

    public NeuronProperties(Class<? extends Neuron> neuronClass,
            Class<? extends InputFunction> inputFunctionClass,
            Class<? extends TransferFunction> transferFunctionClass) {
        initKeys();
        this.setProperty("inputFunction", inputFunctionClass);
        this.setProperty("transferFunction", transferFunctionClass);
        this.setProperty("neuronType", neuronClass);
    }

    public NeuronProperties(Class<? extends Neuron> neuronClass, TransferFunctionType transferFunctionType) {
        initKeys();
        this.setProperty("inputFunction", WeightedSum.class);
        this.setProperty("transferFunction", transferFunctionType.getTypeClass());
        this.setProperty("neuronType", neuronClass);
    }

    public NeuronProperties(TransferFunctionType transferFunctionType, boolean useBias) {
        initKeys();
//		this.setProperty("weightsFunction", WeightedInput.class);
//		this.setProperty("summingFunction", Sum.class);
        this.setProperty("inputFunction", WeightedSum.class);
        this.setProperty("transferFunction", transferFunctionType.getTypeClass());
        this.setProperty("useBias", useBias);
        this.setProperty("neuronType", Neuron.class);
    }

    // uraditi override za setProperty tako da za enum type uzima odgovarajuce klase
    private void initKeys() {
        createKeys("inputFunction", "transferFunction", "neuronType", "useBias"); // use bias prebaciti u NeuralNetworkProperties
    }

    public Class getInputFunction() {
        Object val = this.get("inputFunction");
        if (!val.equals("")) {
            return (Class) val;
        }
        return null;
    }

    public Class getTransferFunction() {
        return (Class) this.get("transferFunction");
    }

    public Class getNeuronType() {
        return (Class) this.get("neuronType");
    }

    public Properties getTransferFunctionProperties() {
        Properties tfProperties = new Properties();
        //Enumeration<?> en = this.keys(); 
        Iterator iterator =  this.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next().toString();
            if (name.contains("transferFunction")) {
                tfProperties.setProperty(name, this.get(name));
            }
        }
        return tfProperties;
    }

    @Override
    public final void setProperty(String key, Object value) {
//                if (!this.containsKey(key))
//                    throw new RuntimeException("Unknown property key: "+key);

        if (value instanceof TransferFunctionType) {
            value = ((TransferFunctionType) value).getTypeClass();
        }
        //      if (value instanceof InputFunctionType) value = ((InputFunctionType)value).getTypeClass();

        this.put(key, value);
    }
}