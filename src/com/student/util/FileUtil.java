
package com.student.util;

import com.student.entity.SchoolClass;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    static {
        File dir = new File(Constant.FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveData(Object data, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    // 修改方法签名，明确指定使用我们的Class类
    public static void saveClasses(List<SchoolClass> classes) {
        saveData(classes, Constant.CLASS_FILE);
    }

    // 修改方法签名，明确指定使用我们的Class类
    public static List<SchoolClass> loadClasses() {
        List<SchoolClass> classes = loadData(Constant.CLASS_FILE);
        return classes != null ? classes : new ArrayList<>();
    }
}