import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Created by twang7 on 4/12/2016.
 */
public class Zero_Crossings implements PlugInFilter {
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ByteProcessor byteProcessor = zeroCrossings(imageProcessor);
        new ImagePlus("zero crossings", byteProcessor).show();

    }

    public ByteProcessor zeroCrossings(ImageProcessor imageProcessor){
        ByteProcessor result = new ByteProcessor(imageProcessor.getWidth(), imageProcessor.getHeight());
        for (int i = 1; i < imageProcessor.getWidth()-1; i++){
            for (int j = 1; j < imageProcessor.getHeight()-1; j++){
                if (imageProcessor.get(i+1, j)*imageProcessor.get(i-1, j)< 0
                        || imageProcessor.get(i, j+1)*imageProcessor.get(i, j-1) < 0){
                    result.set(i, j, 255); // set to 255 may be better, my eyes can not distinguish 1 and 0 :)
                }
                else{
                    result.set(i, j, 0);
                }
            }
        }
        return result;
    }
}
