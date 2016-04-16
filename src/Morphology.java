import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.plugin.filter.GaussianBlur;

/**
 * Created by Tianyang on 4/13/2016.
 */
public class Morphology implements PlugInFilter {
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        Circular_Hough circular_hough = new Circular_Hough();
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        ImageProcessor temp = imp.getProcessor().duplicate();
        temp.threshold(50);
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.run(temp);
        temp.erode();
        temp.threshold(100);
        temp.findEdges();
        circular_hough.doCircular_Hough(temp, temp);
        //new ImagePlus("edges", temp).show();

    }
}
