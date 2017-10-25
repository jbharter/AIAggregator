package com.github.jbharter;


public class FasterFFT {

    public native int sendMeAnInt(char[] mutableMaybe);

    public static void main(String[] args) {
        System.out.println("Here");


        System.load("/Users/jharte/NotWork/AIAggregator/src/main/C++/libfftlib.jnilib");

        FasterFFT fasterFFT = new FasterFFT();

        String toMutable = "Jacob";

        char[] chars = new char[toMutable.length()];

        for (int i = 0; i < chars.length; ++i) {
            chars[i] = toMutable.charAt(i);
        }

        int resp = fasterFFT.sendMeAnInt(chars);

        String r = new String(chars);

        System.out.println(r);

        System.out.println(fasterFFT.sendMeAnInt(new char[]{}));

    }
}
