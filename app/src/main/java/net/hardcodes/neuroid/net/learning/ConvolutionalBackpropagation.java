package net.hardcodes.neuroid.net.learning;

import net.hardcodes.neuroid.core.Layer;
import net.hardcodes.neuroid.core.Neuron;
import net.hardcodes.neuroid.net.comp.layer.ConvolutionalLayer;

public class ConvolutionalBackpropagation extends MomentumBackpropagation {

	private static final long serialVersionUID = -7134947805154423695L;

        @Override
	protected void calculateErrorAndUpdateHiddenNeurons() {
		Layer[] layers = neuralNetwork.getLayers();
		for (int layerIdx = layers.length - 2; layerIdx > 0; layerIdx--) {
			for (Neuron neuron : layers[layerIdx].getNeurons()) {
				double neuronError = this.calculateHiddenNeuronError(neuron);
				neuron.setError(neuronError);
				if (layers[layerIdx] instanceof ConvolutionalLayer) { // if it is convolutional layer c=adapt weughts, dont touch pooling. Pooling just propagate the error
					this.updateNeuronWeights(neuron);
				}
			} // for
		} // for
	}

//	@Override
//	protected double calculateHiddenNeuronError(Neuron neuron) {
//		double totalError = super.calculateHiddenNeuronError(neuron);
//
//        if (neuron.getParentLayer() instanceof  Layer2D) {
//            Layer2D parentLayer = (Layer2D) neuron.getParentLayer();
//            double weight = parentLayer.getHeight() * parentLayer.getWidth();
//            return totalError / weight;
//        }
//        return totalError;
//	}

}
