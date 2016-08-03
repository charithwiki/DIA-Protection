package edu.usc.cps.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by charith on 8/3/16.
 */
public final class Utils {



    public static Set<Integer> getRandomSet(int count, int N) {
        Set<Integer> set = new HashSet<>();

        Random random = new Random();
        while (set.size() != count) {

            int v = random.nextInt(N)  + 1;
            set.add(v);

        }

        return set;
    }
}
