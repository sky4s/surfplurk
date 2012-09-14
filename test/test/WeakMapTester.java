/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 *
 * @author SkyforceShen
 */
public class WeakMapTester {

    static class Main {

        Double d = new Double(1.1);
    }

    public static void main(String[] args) throws InterruptedException {
        WeakHashMap<Integer, Double> map = new WeakHashMap<>();
        List<Main> mainList = new ArrayList<>();
        for (int x = 0; x < 1000; x++) {
            Main m = new Main();
            mainList.add(m);
            map.put(x, m.d);
            System.out.println(map.size());
            Thread.currentThread().sleep(1);
            System.gc();

//            m.d = null;
//            m = null;
        }
        System.out.println(map.size());
    }
}
