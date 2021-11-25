package compiler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config {

    /**
     * 源代码: -s
     */
    private InputStream source = null;

    /**
     * 解释执行的标准输入: -i
     */
    private InputStream input = null;

    public enum Operation {
        TOKENIZE("-T"),
        SYNTAX("-S"),
        ERROR("-E"),
        MID_CODE("-M"),
        VM_RUNNER("-V"),
        OBJECT_CODE("-O"),
        EXCEPTION("-X")
        ;

        private final String option;

        Operation(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }

    private final Map<Operation, PrintStream> operationTarget = new HashMap<>();

    public InputStream getSource() {
        return source;
    }

    public void setSource(InputStream source) {
        this.source = source;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public boolean hasTarget(Operation op) {
        return operationTarget.containsKey(op);
    }

    public PrintStream getTarget(Operation op) {
        return operationTarget.get(op);
    }

    public void setTarget(Operation op, PrintStream target) {
        operationTarget.put(op, target);
    }

    public Config() {}

    // 加载命令行参数得到配置类, 如果参数不合法(出错)则返回 null
    public static Config fromArgs(String[] args) {
        Config config = new Config();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) { // source file
                if (Objects.nonNull(config.getSource())) {
                    System.err.println("Only 1 source file supported.");
                    return null;
                }
                if (i + 1 >= args.length) {
                    System.err.println("-s needs source file.");
                    return null;
                }
                String filename = args[i + 1];
                i++;
                try {
                    FileInputStream input = new FileInputStream(filename);
                    config.setSource(input);
                } catch (FileNotFoundException e) {
                    System.err.println("Cannot open source file: " + filename);
                    return null;
                }
            } else if (args[i].equals("-i")) { // input file
                if (Objects.nonNull(config.getInput())) {
                    System.err.println("Only 1 input file supported.");
                    return null;
                }
                if (i + 1 >= args.length) {
                    System.err.println("-i needs input file.");
                    return null;
                }
                String filename = args[i + 1];
                i++;
                try {
                    FileInputStream input = new FileInputStream(filename);
                    config.setInput(input);
                } catch (FileNotFoundException e) {
                    System.err.println("Cannot open input file: " + filename);
                    return null;
                }
            } else { // target output
                boolean isTarget = false;
                for (Operation op : Operation.values()) {
                    if (args[i].equals(op.getOption())) {
                        isTarget = true;
                        if (Objects.nonNull(config.getTarget(op))) {
                            System.err.println("Duplicated target option: " + op.getOption());
                            return null;
                        }
                        if (i + 1 >= args.length) {
                            System.err.println(op.getOption() + " needs output file.");
                            return null;
                        }
                        String filename = args[i + 1];
                        i++;
                        try {
                            FileOutputStream output = new FileOutputStream(filename);
                            config.setTarget(op, new PrintStream(output));
                        } catch (FileNotFoundException e) {
                            System.err.println("Cannot open output file of target " + op.name() + ": " + filename);
                            return null;
                        }
                    }
                }
                if (!isTarget) {
                    System.err.println("Can't resolve option " + args[i]);
                    return null;
                }
            }
        }
        // default source and input
        if (Objects.isNull(config.source)) {
            String source = "testfile.txt";
            try {
                FileInputStream inputSource = new FileInputStream(source);
                config.setSource(inputSource);
            } catch (FileNotFoundException e) {
                System.err.println("Cannot open default source file: " + source);
                return null;
            }
        }
        if (Objects.isNull(config.input)) {
            config.setInput(System.in);
        }
        return config;
    }

    /**
     * 打印命令行参数说明
     * @return 参数说明
     */
    public static String usage() {
        String header = "java -jar Compiler.jar -s source_file [-i input_file] [targets]\nTargets: \n";
        StringBuilder sb = new StringBuilder();
        for (Operation op : Operation.values()) {
            sb.append(String.format("    %-2s [output_file] : %s", op.getOption(), op.name())).append("\n");
        }
        return header + sb;
    }
}
