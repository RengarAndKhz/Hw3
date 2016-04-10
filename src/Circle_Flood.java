/**
 * Created by Tianyang on 4/9/16.
 */
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Circle_Flood implements PlugInFilter{
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {


    }
}
