package com.mark.prosper.invest.util;

import java.util.Random;

/**
 * Created by NeVeX on 10/19/2015.
 */
public class DebugUtil {

    private static Random random = new Random();
    private static String[] PROSPER_RATINGS = {"AA", "A", "B", "C", "D", "E", "HR"};

    public static String getRandomProsperRating()
    {
        return PROSPER_RATINGS[random.nextInt(7)];
    }

    public static boolean getRandomBoolean() {
        return random.nextInt(2) == 0;
    }
}
