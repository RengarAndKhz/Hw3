/**
 * Created by Tianyang on 4/9/16.
 */
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Circle_Flood implements PlugInFilter{
    final int pMax = 40;
    final int pMin = 10;
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        circularHough();
    }

    public void circularHough(){
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        //ImageConverter ic = new ImageConverter(new ImagePlus("", imp));
        ic.convertToGray8();
        ImageProcessor imageProcessor = imp.getProcessor().duplicate();
        imageProcessor.threshold(80);
        new ImagePlus("", imageProcessor).show();
    }

}
