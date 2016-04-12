/**
 * Created by Tianyang on 4/9/16.
 */
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageStatistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Circle_Flood implements PlugInFilter{
    final int pMax = 40;
    final int pMin = 10;
    final boolean choosingFlag = false;
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }
    /**
     * main method of cirle flood algorithm
     * the background of the binary image is 0 and the foreground is 255
     * the value of the circle which split the resgions is 200
     * @param
     * @param
     * @return
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        List<CircleInfo> dataFromCircularHough = circularHough();
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        imageProcessor = imp.getProcessor().duplicate();
        imageProcessor.threshold(80);
        ImageProcessor binaryImageProcessor = imageProcessor.duplicate();
        new ImagePlus("bianry image", binaryImageProcessor).show();
        if (choosingFlag){
            int result = recursveFloodFill(binaryImageProcessor, dataFromCircularHough);
            new ImagePlus(Integer.toString(result), binaryImageProcessor).show();
        }
        else {
            coherenceMethod(binaryImageProcessor);
            new ImagePlus("coherence method", binaryImageProcessor).show();
        }
    }



    /**
     * naive recursive flood fill algorithm to fill the regions
     * @param imageProcessor
     * @return
     */
    public int recursveFloodFill(ImageProcessor imageProcessor, List<CircleInfo> dataFromCircularHough){
        int label = 1; // initial the value of the next label to be assigned
        for (CircleInfo curr : dataFromCircularHough){
            for (int i = 0; i < imageProcessor.getWidth(); i++){
                for (int j = 0; j < imageProcessor.getHeight(); j++){
                    if (imageProcessor.get(i, j)  != 0 && isInCircle(i, j, curr)){
                        floodFill(imageProcessor, i, j, label, curr);
                        label++;
                    }
                }
            }
        }
        return label;
    }

    public void floodFill(ImageProcessor imageProcessor, int i, int j, int label, CircleInfo info){
        if (isInCircle(i, j, info) && imageProcessor.get(i, j) == 255){
            imageProcessor.set(i, j, label);
            floodFill(imageProcessor, i+1, j, label, info);
            floodFill(imageProcessor, i, j+1, label, info);
            floodFill(imageProcessor, i, j-1, label, info);
            floodFill(imageProcessor, i-1, j, label, info);
        }
        return;
    }

    /**
     * the Coherence method to fill the regions
     * @param
     * @return
     */
    public void coherenceMethod(ImageProcessor imageProcessor){

        int label = 1; //initiate the value of label
        for (int i = 0; i < imageProcessor.getWidth(); i++){
            for (int j = 0; j < imageProcessor.getHeight(); j++){
                //search the seed in the whole pic
                if (imageProcessor.get(i, j) == 255){
                    Stack<Pair<Integer, Integer>> stack = new Stack<Pair<Integer, Integer>>();
                    // put the original seed in the stack
                    stack.add(new Pair<Integer, Integer>(i, j));
                    while (!stack.isEmpty()){
                        Pair<Integer, Integer> currPair = stack.pop();
                        doCoherenceFilling(currPair, stack, imageProcessor, label);
                    }
                    label++;
                }

            }
        }
    }

    /**
     * helper function, fill the line and detect the seeds
     * @param seed
     * @param stack
     * @param imageProcessor
     * @param label
     */

    public void doCoherenceFilling(Pair<Integer, Integer> seed, Stack<Pair<Integer, Integer>> stack, ImageProcessor imageProcessor, int label){
        int i = seed.getKey(); int j = seed.getValue();
        List<Pair<Integer, Integer>> testList = new ArrayList<Pair<Integer, Integer>>();
        testList.add(getAboveSeed(i, j, imageProcessor));
        testList.add(getBelowSeed(i, j, imageProcessor));
        int rloffset = 0;
        int lroffset = 0;
        //right to left filling
        while (imageProcessor.get(i - rloffset, j) != 0) {
            imageProcessor.set(i - rloffset, j, label);
            if (!getAboveSeed(i - rloffset, j, imageProcessor).equals(testList.get(0))) testList.add(0, getAboveSeed(i-rloffset, j, imageProcessor));
            if (!getBelowSeed(i - rloffset, j, imageProcessor).equals(testList.get(testList.size() - 1))) testList.add(getBelowSeed(i-rloffset, j, imageProcessor));
            rloffset++;
        }
        // left to right filling
        while (imageProcessor.get(i + lroffset, j) != 0) {
            imageProcessor.set(i + lroffset, j, label);
            if (!getAboveSeed(i + lroffset, j, imageProcessor).equals(testList.get(0))) testList.add(0, getAboveSeed(i+lroffset, j, imageProcessor));
            if (!getBelowSeed(i + lroffset, j, imageProcessor).equals(testList.get(testList.size() - 1))) testList.add(getBelowSeed(i+lroffset, j, imageProcessor));
            lroffset++;
        }
        //add seed into the stack
        for (Pair<Integer, Integer> curr : testList){
            stack.add(curr);
        }
    }

    public Pair<Integer, Integer> getAboveSeed(int i, int j, ImageProcessor imageProcessor){
        j = j + 1;
        while (imageProcessor.get(i, j) != 0) i++;
        return new Pair<Integer, Integer>(i-1, j);

    }

    public  Pair<Integer, Integer> getBelowSeed(int i, int j, ImageProcessor imageProcessor){
        j = j - 1;
        while (imageProcessor.get(i, j) != 0) i++;
        return new Pair<Integer, Integer>(i-1, j);
    }


    public List<CircleInfo> circularHough(){
        List<CircleInfo> resultList = new ArrayList<CircleInfo>();
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        //ImageConverter ic = new ImageConverter(new ImagePlus("", imp));
        ic.convertToGray8();
        ImageProcessor imageProcessor = imp.getProcessor().duplicate();
        ImageProcessor result = imp.getProcessor().duplicate();
        imageProcessor.threshold(80);
        ImageProcessor binaryImageProcessor = imageProcessor.duplicate();
        //new ImagePlus("Binary image", binaryImageProcessor).show();
        imageProcessor.findEdges();
        //new ImagePlus("edges", imageProcessor).show();
        List<ImageProcessor> rList = new ArrayList<ImageProcessor>();
        for (int i = 0; i < pMax - pMin; i++){
            rList.add(imageProcessor.duplicate());
        }
        /*
         * change r in every duplicate imageProcessor
         * increasing rate = 1
         */
        for (int i = 0; i < imageProcessor.getHeight(); i++){
            for (int j = 0; j < imageProcessor.getWidth(); j++){
                if (imageProcessor.get(j, i) == 255){
                    for (int index = 0; index < pMax - pMin; index++){
                        int r = index + pMin;
                        Circular_Hough.incrementCircle(rList.get(index), j, i, r);
                    }
                }
            }
        }
        ImageStack rStack = new ImageStack(imageProcessor.getWidth(), imageProcessor.getHeight());
        for (int i = 0; i < pMax - pMin; i++){
            ImageProcessor temProcessor = rList.get(i);
            rStack.addSlice(temProcessor);
        }
        //new ImagePlus("different circles", rStack).show();

        for (int index = 0; index < pMax - pMin; index++){
            ImageProcessor tempForDrawingCircle = rStack.getProcessor(index + 1);
            for (int i = 0; i < imageProcessor.getHeight(); i++){
                for (int j = 0; j < imageProcessor.getWidth(); j++){
                    if (tempForDrawingCircle.get(j, i) > 3.3 * (index + pMin) && tempForDrawingCircle.get(j, i) < 100){
                        tempForDrawingCircle.set(j, i, 255);
                        resultList.add(new CircleInfo(j, i, index + pMin));
                        Circular_Hough.drawCircle(binaryImageProcessor, j, i, index + pMin, 200);
                    }
                }
            }
        }
        //new ImagePlus("result", binaryImageProcessor).show();
        return resultList;

    }

    /**
     * detect if the pixel is in the circle
     * @param i
     * @param j
     * @param info
     * @return boolean type
     */
    public boolean isInCircle(int i, int j, CircleInfo info){
        int w = info.getI();
        int h = info.getJ();
        int radi = info.getRadi();
        if ((i - w) * (i - w) + (j - h) * (j - h) <= radi * radi){
            return true;
        }
        else return false;

    }



}
