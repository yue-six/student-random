
package com.student;

import com.student.view.MainFrame;
import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        try {
            // 设置本地系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在EDT线程中创建和显示GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}