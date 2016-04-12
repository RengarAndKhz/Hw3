import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by twang7 on 4/11/2016.
 */
public class MainTest {
    public static void main(String[] args){
        Pair<Integer, Integer> pair = new Pair<>(1, 2);
        //System.out.println(pair.);
        String[] list = {"a", "b", "c", "a"};
        List<String> testList = new ArrayList<>();
        testList.add("a");
        testList.add(0, "b");
        for (String curr : testList){
            System.out.println(curr);
        }
    }
}
