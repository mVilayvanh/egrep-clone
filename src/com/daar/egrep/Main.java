package com.daar.egrep;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if  (args.length != 2) {
            System.err.println("Usage:");
            System.err.println("java -jar EGrep.jar [pattern] [filepath]");
        } else {
            EGrep e = new EGrep(args[0], args[1]);
            e.runAndPrint();
        }
    }
}
