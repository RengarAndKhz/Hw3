/**
 * Created by twang7 on 4/12/2016.
 */
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


public class Gradient_Magnitude implements PlugInFilter{
    final double sigma = 2.0;

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        FloatProcessor floatProcessor = (FloatProcessor) imageProcessor.convertToFloat();
        gradientMagnitude(floatProcessor, sigma);
        ByteProcessor result = new ByteProcessor(floatProcessor, true);
        imageProcessor.copyBits(result, 0, 0, Blitter.COPY);


    }

    /**
     * compute gradient magnitude
     * j = sqrt(sqr(Ix convolve G`) + sqr(Iy convolve G`))
     * @param floatProcessor
     * @param sigma
     */
    public void gradientMagnitude(ImageProcessor floatProcessor, double sigma){

        float[] kernel = makeGaussianKernel1d(sigma);
        float[] kernelDerive = deriveKernel(kernel);
        //float[] kernelDerive = kernel;

        ImageProcessor xDirection = floatProcessor.duplicate();
        ImageProcessor yDirection = floatProcessor.duplicate();

        xDirection.convolve(kernelDerive, 1, kernelDerive.length);
        xDirection.sqr();
        new ImagePlus("x direction", xDirection).show();
        yDirection.convolve(kernelDerive, kernelDerive.length, 1);
        yDirection.sqr();
        new ImagePlus("y direction", yDirection).show();
        xDirection.copyBits(yDirection, 0, 0, Blitter.ADD);
        new ImagePlus("add", xDirection).show();
        xDirection.sqrt();
        //new ImagePlus("after gradient Magnitude", xDirection).show();

        floatProcessor.setPixels(xDirection.getPixels());
        //new ImagePlus("after gradient", floatProcessor).show();
    }

    /**
     * i did not find a efficient way to compute the derivative of the Gaussian kernel
     * however, I googled a very good method to compute this
     * @param kernel
     * @return
     */
    public float[] deriveKernel(float[] kernel){
        float[] tempKernel = new float[kernel.length + 2];
        tempKernel[0] = 0.0f;
        for (int i = 1; i <= kernel.length; i++)
            tempKernel[i] = kernel[i - 1];
        tempKernel[tempKernel.length - 1] = 0.0f;

        float[] kernelDerivative = new float[kernel.length];

        int loop = (int) Math.ceil(kernelDerivative.length / 2.0);
        for (int i = 0; i < loop - 1; i++) {
            kernelDerivative[i] = tempKernel[i + 1] - tempKernel[i];
        }

        for (int i = kernelDerivative.length - 1; i > kernelDerivative.length - loop - 1; i--) {
            kernelDerivative[i] = tempKernel[i + 2] - tempKernel[i + 1];
        }

        kernelDerivative[(int) Math.floor(kernelDerivative.length/2.0)] = 0.0f;

        return kernelDerivative;

    }

    public float[] makeGaussianKernel1d(double sigma){

        //create the kernel
        int center = (int) (3.0*sigma);
        float[] kernel = new float[2*center+1];
        double sum = 0.0;

        //fill the kernel
        double sigma2 = sigma*sigma;
        for (int i = 0; i < kernel.length; i++){
            double r = center - i;
            kernel[i] = (float) Math.exp(-0.5 * (r*r) / sigma2);
            //kernel[i] = (float) ((Math.exp(-0.5*(r*r)/sigma2) * (-r/sigma2)));
            sum += kernel[i];
        }
        //normalize the kernel
        for (int i = 0; i < kernel.length; i++){
            kernel[i] = (float) (kernel[i]/sum);
        }
        return kernel;
    }
}
