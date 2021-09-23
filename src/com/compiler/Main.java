package com.compiler;

import com.compiler.args.Global;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        // load arguments
        try {
            if (args.length > 0) { Global.loadArgs(args); }
            else { Global.loadArgs(new String[]{"-T", "-i", "testfile.txt"}); }
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        // read source Code

    }
}
