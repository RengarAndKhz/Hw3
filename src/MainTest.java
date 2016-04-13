import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by twang7 on 4/11/2016.
 */
public class MainTest {
    public static float[] derive(float[] H) {

        float[] H_pad = new float[H.length + 2];
        H_pad[0] = 0.0f;
        for (int i = 1; i <= H.length; i++)
            H_pad[i] = H[i - 1];
        H_pad[H_pad.length - 1] = 0.0f;

        float[] new_h = new float[H_pad.length - 2];

        int loop = (int) Math.ceil(new_h.length / 2.0);
        System.out.println(loop);
        for (int i = 0; i < loop - 1; i++) {
            new_h[i] = H_pad[i + 1] - H_pad[i];
        }

        for (int i = new_h.length - 1; i > new_h.length - loop - 1; i--) {
            new_h[i] = H_pad[i + 2] - H_pad[i + 1];
        }

        return new_h;
    }


    public static void main(String[] args){
        float[] testList = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        for (float curr : derive(testList)){
            System.out.println(curr);
        }
    }
}
