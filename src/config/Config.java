package config;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Config {

    /**
     * 参数指定输入源, 以 -i 开头
     */
    private static InputStream source = System.in;

    /**
     * 参数指定输出目标, 以 -o 开头
     */
    private static PrintStream target = System.out;

    public enum Operation {
        TOKENIZE("T"),
        SYNTAX("S"),
        ERROR("E"),
        INTERMEDIATE("I"),
        VIRTUAL_MACHINE("V"),
        OBJECT("O")
        ;

        private final String tag;

        Operation(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    /**
     * 指定编译器执行到的步骤, 为第一个命令行参数, 为 "-" + 对应枚举对象的 tag, 例如 "-T"
     */
    private static final Set<Operation> outputOperations = new HashSet<>();

    public static InputStream getSource() {
        return source;
    }

    private static void setSource(InputStream source) {
        Config.source = source;
    }

    public static PrintStream getTarget() {
        return target;
    }

    private static void setTarget(OutputStream target) {
        Config.target = new PrintStream(target);
    }

    public static boolean hasOperationOutput(Operation operation) {
        return outputOperations.contains(operation);
    }

    private static void addOperation(Operation operation) {
        outputOperations.add(operation);
    }

    public static void loadArgs(String[] args) throws FileNotFoundException {
        boolean hasInputFile = false;
        boolean hasOutputFile = false;
        for (int i = 0; i < args.length; i++) {
            for (Operation elem : Operation.values()) {
                if (args[i].equals("-" + elem.getTag())) {
                    addOperation(elem);
                }
            }
            if (args[i].equals("-i")) {
                if (hasInputFile) {
                    System.err.println("Warning: only 1 input file supported.");
                    continue;
                }
                if (i + 1 < args.length) {
                    String filename = args[i + 1];
                    try {
                        InputStream input = new FileInputStream(filename);
                        setSource(input);
                        hasInputFile = true;
                    } catch (FileNotFoundException e) {
                        System.err.println("Error: file \"" + filename + "\" not found");
                        throw e;
                    }
                } else {
                    System.err.println("\"-i\" need a file name.");
                }
            }
            if (args[i].equals("-o")) {
                if (hasOutputFile) {
                    System.err.println("Warning: only 1 output file supported.");
                    continue;
                }
                if (i + 1 < args.length) {
                    String filename = args[i + 1];
                    try {
                        OutputStream output = new FileOutputStream(filename);
                        setTarget(output);
                        hasOutputFile = true;
                    } catch (FileNotFoundException e) {
                        System.err.println("Error: file \"" + filename + "\" cannot open.");
                        throw e;
                    }
                } else {
                    System.err.println("\"-o\" need a file name.");
                }
            }
        }
    }
}
