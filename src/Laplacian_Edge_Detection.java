import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

/**
 * Created by twang7 on 4/12/2016.
 */
public class Laplacian_Edge_Detection implements PlugInFilter {
    final double sigma = 3.0;
    Gradient_Magnitude gradient_magnitude = new Gradient_Magnitude();
    Laplacian_Image laplacian_image = new Laplacian_Image();
    Zero_Crossings zero_crossings = new Zero_Crossings();
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        ImageProcessor temp = imp.getProcessor();

        FloatProcessor floatProcessor = (FloatProcessor) temp.convertToFloat();
        FloatProcessor tempForGradient = (FloatProcessor) floatProcessor.duplicate();
        gradient_magnitude.gradientMagnitude(tempForGradient, sigma);
        setThreshold(tempForGradient, 8.0);
        new ImagePlus("after threshold", tempForGradient).show();
        FloatProcessor tempForLaplacian = (FloatProcessor) floatProcessor.duplicate();
        laplacian_image.laplacianImage(tempForLaplacian, sigma);
        ByteProcessor bitImage = zero_crossings.zeroCrossings(tempForLaplacian);
        ByteProcessor result = new ByteProcessor(tempForGradient, true);
        result.copyBits(bitImage, 0, 0, Blitter.AND);
        //imageProcessor.copyBits(result, 0, 0, Blitter.COPY);
        new ImagePlus("result", result).show();

    }
    public void setThreshold(FloatProcessor floatProcessor, double threshold){
        for (int i = 0; i < floatProcessor.getWidth(); i++){
            for (int j = 0; j < floatProcessor.getHeight(); j++){
                if (floatProcessor.getPixelValue(i, j) < threshold) floatProcessor.putPixelValue(i, j, 0);
                else floatProcessor.putPixelValue(i, j, 255);
            }
        }
    }
}
