/**
 * Created by Tianyang on 2016/4/7.
 */
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.plugin.filter.GaussianBlur;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Circular_Hough implements PlugInFilter{
    final int pMax = 40;
    final int pMin = 20; //coins: 10, pupils: 20
    final int threshold = 80;
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ImagePlus imp = IJ.getImage();
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        ImageProcessor temp = imp.getProcessor().duplicate();
        ImageProcessor result = imp.getProcessor().duplicate();
        doCircular_Hough(temp, result);

    }
    public void doCircular_Hough(ImageProcessor temp, ImageProcessor result){
        temp.threshold(threshold);
        temp.findEdges();
        new ImagePlus("", temp).show();

        List<ImageProcessor> circleList= new ArrayList<ImageProcessor>();
        for (int index = 0; index < pMax - pMin; index++){
            circleList.add(temp.duplicate());
        }
        for (int i = 0; i < temp.getHeight(); i++){
            for (int j = 0; j < temp.getWidth(); j++){
                if (temp.get(j, i) == 255){
                    for (int index = 0; index < pMax - pMin; index++){
                        int r = index + pMin;
                        incrementCircle(circleList.get(index), j, i, r);

                    }
                }
            }
        }

        ImageStack showTest = new ImageStack(temp.getWidth(), temp.getHeight());
        //int counter = 0;
        List<CircleInfo> circleInfos = new ArrayList<CircleInfo>();


        for (int index = 0; index < pMax - pMin; index++){
            ImageProcessor tempProcessor = circleList.get(index);
            showTest.addSlice(temp.duplicate());
            ImageProcessor showTestProcessor = showTest.getProcessor(index+1);

            for (int i = 0; i < temp.getHeight(); i++){
                for (int j = 0; j < temp.getWidth(); j++){
                    if (tempProcessor.get(j, i) > 4.5 * (index + pMin) && tempProcessor.get(j, i) < 250 && removeDuplicateCenter(tempProcessor, j, i)){

                        showTestProcessor.set(j, i, 255);
                        drawCircle(result, j, i, index+pMin, 100);
                        circleInfos.add(new CircleInfo(j, i, index+pMin));
                    }

                }
            }
        }

        ImageStack test = new ImageStack(temp.getWidth(), temp.getHeight());
        for (int index = 1; index <= pMax - pMin; index++){
            test.addSlice(circleList.get(index-1));
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (CircleInfo curr : circleInfos){
            stringBuffer.append(curr.getRadi());
            stringBuffer.append(',');
        }
        new ImagePlus("", test).show();
        new ImagePlus("hough", showTest).show();
        new ImagePlus("result" + " Radi" + stringBuffer.toString(), result).show();

    }

    /**
     * implement Breshenham Circles
     * @param p
     * @param i
     * @param j
     * @param r
     */

    public static void incrementCircle(ImageProcessor p, int i, int j, int r) {

        int x = 0;
        int y = r;

        double flag = -r + 0.25;

        p.putPixelValue(i+x, j+y, p.getPixelValue(i+x, j+y)+1);
        p.putPixelValue(i+y, j+x, p.getPixelValue(i+y, j+x)+1);
        p.putPixelValue(i+x, j-y, p.getPixelValue(i+x, j-y)+1);
        p.putPixelValue(i-y, j+x, p.getPixelValue(i-y, j+x)+1);

        while (x < y-1) {
            flag = flag + 2 * x + 1;
            x = x + 1;
            if (flag >= 0) {
                flag = flag - 2 * y + 2;
                y = y - 1;
            }
            p.putPixelValue(i+x, j+y, p.getPixelValue(i+x, j+y)+1);
            p.putPixelValue(i+y, j+x, p.getPixelValue(i+y, j+x)+1);
            p.putPixelValue(i-x, j+y, p.getPixelValue(i-x, j+y)+1);
            p.putPixelValue(i+y, j-x, p.getPixelValue(i+y, j-x)+1);
            p.putPixelValue(i+x, j-y, p.getPixelValue(i+x, j-y)+1);
            p.putPixelValue(i-y, j+x, p.getPixelValue(i-y, j+x)+1);
            p.putPixelValue(i-x, j-y, p.getPixelValue(i-x, j-y)+1);
            p.putPixelValue(i-y, j-x, p.getPixelValue(i-y, j-x)+1);
        }
    }

    /**
     * using build-in method drawoval to draw the circle
     * @param ip
     * @param u
     * @param v
     * @param r
     * @param value
     */
    public static void drawCircle(ImageProcessor ip, int u, int v, int r, int value){
        ip.setValue(value);
        ip.drawOval(u-r, v-r, 2*r, 2*r);
    }

    /**
     * change the local highest value in 9*9 matrix, it perform not very well sometimes
     * @param imageProcessor
     * @param x
     * @param y
     */
    public boolean removeDuplicateCenter(ImageProcessor imageProcessor, int x, int y){
        float pixelValue = imageProcessor.getPixelValue(x, y);
        // check the value in 9*9 matrix
        if ( pixelValue <= imageProcessor.getPixelValue(x+1, y  ) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x+1, y+1) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x  , y+1) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x-1, y+1) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x-1, y  ) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x-1, y-1) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x  , y-1) ) return false;
        if ( pixelValue <= imageProcessor.getPixelValue(x+1, y-1) ) return false;
        return true;
    }





}