/**
 * Created by Tianyang on 2016/4/7.
 */
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Circular_Hough implements PlugInFilter{
    final int pMax = 40;
    final int pMin = 10;
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
        temp.threshold(80);
        temp.findEdges();
        new ImagePlus("", temp).show();

        List<ImageProcessor> circleList= new ArrayList<>();
        for (int index = 0; index < pMax - pMin; index++){
            circleList.add(temp.duplicate());
        }
        //stack.getProcessor(10);
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
        int counter = 0;


        for (int index = 0; index < pMax - pMin; index++){
            ImageProcessor tempProcessor = circleList.get(index);
            showTest.addSlice(temp.duplicate());
            ImageProcessor showTestProcessor = showTest.getProcessor(index+1);

            for (int i = 0; i < temp.getHeight(); i++){
                for (int j = 0; j < temp.getWidth(); j++){
                    if (tempProcessor.get(j, i) > 3.2 * (index + pMin) && tempProcessor.get(j, i) < 100){
                        showTestProcessor.set(j, i, 255);
                        drawCircle(result, j, i, index+pMin, 200);
                    }

                }
            }
        }

        ImageStack test = new ImageStack(temp.getWidth(), temp.getHeight());
        for (int index = 1; index <= pMax - pMin; index++){
            test.addSlice(circleList.get(index-1));
        }
        new ImagePlus("", test).show();
        new ImagePlus(Integer.toString(counter), showTest).show();
        new ImagePlus("", result).show();


    }

    private void incrementCircle(ImageProcessor p, int u, int v, int r) {

        int x = 0;
        int y = r;

        double F = -r + 0.25;

        p.putPixelValue(u+x, v+y, p.getPixelValue(u+x, v+y)+1);
        p.putPixelValue(u+y, v+x, p.getPixelValue(u+y, v+x)+1);
        p.putPixelValue(u+x, v-y, p.getPixelValue(u+x, v-y)+1);
        p.putPixelValue(u-y, v+x, p.getPixelValue(u-y, v+x)+1);

        while (x < y-1) {

            F = F + 2 * x + 1;
            x = x + 1;

            if (F >= 0) {
                F = F - 2 * y + 2;
                y = y - 1;
            }

            p.putPixelValue(u+x, v+y, p.getPixelValue(u+x, v+y)+1);
            p.putPixelValue(u+y, v+x, p.getPixelValue(u+y, v+x)+1);
            p.putPixelValue(u-x, v+y, p.getPixelValue(u-x, v+y)+1);
            p.putPixelValue(u+y, v-x, p.getPixelValue(u+y, v-x)+1);

            p.putPixelValue(u+x, v-y, p.getPixelValue(u+x, v-y)+1);
            p.putPixelValue(u-y, v+x, p.getPixelValue(u-y, v+x)+1);
            p.putPixelValue(u-x, v-y, p.getPixelValue(u-x, v-y)+1);
            p.putPixelValue(u-y, v-x, p.getPixelValue(u-y, v-x)+1);
        }

    }
    public static void drawCircle(ImageProcessor ip, int u, int v, int r, int value){
        ip.setValue(value);
        ip.drawOval(u-r, v-r, 2*r, 2*r);
    }


}