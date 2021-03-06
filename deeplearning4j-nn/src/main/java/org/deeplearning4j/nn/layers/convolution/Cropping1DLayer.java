package org.deeplearning4j.nn.layers.convolution;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.convolutional.Cropping1D;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.layers.AbstractLayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;

import static org.nd4j.linalg.indexing.NDArrayIndex.all;
import static org.nd4j.linalg.indexing.NDArrayIndex.interval;

/**
 * Zero cropping layer for 1D convolutional neural networks.
 * Allows cropping to be done separately for top/bottom
 *
 * @author Max Pumperla
 */
public class Cropping1DLayer extends AbstractLayer<Cropping1D> {

    private int[] cropping; //[padTop, padBottom]

    public Cropping1DLayer(NeuralNetConfiguration conf) {
        super(conf);
        this.cropping = ((org.deeplearning4j.nn.conf.layers.convolutional.Cropping1D) conf.getLayer()).getCropping();
    }
    @Override
    public INDArray preOutput(boolean training) {
        return activate(training);
    }

    @Override
    public boolean isPretrainLayer() {
        return false;
    }

    @Override
    public void clearNoiseWeightParams() {
        //No op
    }

    @Override
    public Type type() {
        return Type.CONVOLUTIONAL;
    }

    @Override
    public Pair<Gradient, INDArray> backpropGradient(INDArray epsilon) {
        int[] inShape = input.shape();
        INDArray epsNext = Nd4j.create(inShape, 'c');
        INDArray epsNextSubset = inputSubset(epsNext);
        epsNextSubset.assign(epsilon);
        return new Pair<>((Gradient) new DefaultGradient(), epsNext);
    }


    @Override
    public INDArray activate(boolean training) {
        return inputSubset(input);
    }

    @Override
    public Layer clone() {
        return new Cropping2DLayer(conf.clone());
    }

    @Override
    public double calcL1(boolean backpropParamsOnly) {
        return 0;
    }

    @Override
    public double calcL2(boolean backpropParamsOnly) {
        return 0;
    }

    private INDArray inputSubset(INDArray from){
        return from.get(all(), all(), interval(cropping[0], from.size(2)-cropping[1]));
    }
}
