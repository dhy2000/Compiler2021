package utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 将源代码快速压缩为 zip
 */
public class ZipUtil {

    public static final String ZIP_PATH = "src/";
    public static final String FILE_SUFFIX = ".java";
    public static final String ZIP_NAME = "Compiler2021.zip";

    public static final int BUFFER_SIZE = 2048;

    private static List<File> listFiles(File path, FileFilter filter) {
        File[] files = path.listFiles(filter);
        List<File> list = new ArrayList<>();
        if (Objects.nonNull(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFiles(file, filter));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public static void compress() throws IOException {
        long start = System.currentTimeMillis();

        FileFilter filter = file -> file.isDirectory() || file.getName().endsWith(FILE_SUFFIX);
        List<File> list = listFiles(new File(ZIP_PATH), filter);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(ZIP_NAME));

        for (File file : list) {
            zos.putNextEntry(new ZipEntry(file.getPath()));
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            FileInputStream fin = new FileInputStream(file);
            while ((len = fin.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
            fin.close();
        }

        zos.close();

        long end = System.currentTimeMillis();
        System.out.println("Zip done, elapsed " + (end - start) + "ms.");
    }
}
