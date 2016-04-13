/**
 * Created by twang7 on 4/12/2016.
 */
import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


public class Laplacian_Image implements PlugInFilter{
    final double sigma = 3.0;
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        FloatProcessor floatProcessor = (FloatProcessor) imageProcessor.convertToFloat();
        laplacianImage(floatProcessor, sigma);

    }

    public void laplacianImage(FloatProcessor floatProcessor, double sigma){
        float[] kernel = gaussian2ndDerivativeKernel(sigma);
        ImageProcessor xDirection = floatProcessor.duplicate();
        ImageProcessor yDirection = floatProcessor.duplicate();
        //do convolution
        xDirection.convolve(kernel, 1, kernel.length);
        yDirection.convolve(kernel, kernel.length, 1);
        xDirection.copyBits(yDirection, 0, 0, Blitter.ADD);
        new ImagePlus("laplacian Image", xDirection).show();

        floatProcessor.setPixels(xDirection.getPixels());
    }

    /**
     * implement based on http://www.gris.informatik.tu-darmstadt.de/~akuijper/course/TUD11/lecture3.pdf
     * @param sigma
     * @return
     */
    public float[] gaussian2ndDerivativeKernel(double sigma){
        int center = (int) (3 * sigma);
        float[] kernel = new float[2*center + 1];
        double sigma2 = sigma * sigma;
        double sum = 0.0;
        for (int i = 0; i < kernel.length; i++){
            double r = center - i;
            kernel[i] = (float) (Math.exp(-0.5*(r*r)/sigma2)*((r*r)/(sigma2*sigma2)-1/sigma2));
            sum += kernel[i];
        }
        // normalize kernel
        for (int i = 0; i < kernel.length; i++){
            kernel[i] = (float)(kernel[i]/sum);
        }
        return kernel;
    }
}
