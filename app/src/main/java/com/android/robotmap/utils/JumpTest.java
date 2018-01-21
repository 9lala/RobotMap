package com.android.robotmap.utils;

/**
 * Created by Administrator on 2018/1/2.
 */

public class JumpTest {

    public static void main(String[] args) {
        calc(117, 57);
    }
    private static void calc(double v, double v2) {
        double v3 = hypotenuse(v, v2);
        System.out.println((v3 / 145.619367) * 769 + "");
    }

    public static double hypotenuse(double a, double b) {

        return Math.sqrt(a * a + b * b);

    }
}
