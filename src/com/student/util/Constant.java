package com.student.util;

import java.awt.*;
import java.io.File;

public class Constant {
    // 文件存储相关常量
    public static final String DATA_DIR = "data";
    public static final String FILE_PATH = DATA_DIR + File.separator;
    public static final String CLASS_FILE = FILE_PATH + "classes.dat";

    // 窗口尺寸常量
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;
    public static final int DIALOG_WIDTH = 350;
    public static final int DIALOG_HEIGHT = 300;

    // 面板尺寸常量
    public static final int PANEL_WIDTH = 300;
    public static final int PANEL_HEIGHT = 400;
    public static final int LIST_WIDTH = 200;
    public static final int LIST_HEIGHT = 300;

    // 组件尺寸常量
    public static final int BUTTON_WIDTH = 120;
    public static final int BUTTON_HEIGHT = 30;
    public static final int TEXT_FIELD_WIDTH = 200;
    public static final int TEXT_FIELD_HEIGHT = 25;

    // 边距和间距常量
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int MARGIN_SMALL = 5;
    public static final int MARGIN_MEDIUM = 10;
    public static final int MARGIN_LARGE = 20;

    // 字体相关常量
    public static final String FONT_NAME = "宋体";
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_MEDIUM = 14;
    public static final int FONT_SIZE_LARGE = 16;
    public static final int FONT_SIZE_TITLE = 20;

    // 字体对象常量
    public static final Font FONT_NORMAL = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_MEDIUM);
    public static final Font FONT_BOLD = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_MEDIUM);
    public static final Font FONT_TITLE = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_TITLE);

    // 随机抽取相关常量
    public static final int BLINK_TIMES = 20;
    public static final int BLINK_INTERVAL = 100;

    // 初始化方法：确保数据目录存在
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // 私有构造函数，防止实例化
    private Constant() {
        throw new AssertionError("Constant class cannot be instantiated");
    }
}