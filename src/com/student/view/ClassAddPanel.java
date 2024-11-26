package com.student.view;

import com.student.entity.SchoolClass;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.UUID;

public class ClassAddPanel extends JPanel {
    private ClassService classService;
    private MainFrame mainFrame;
    private JTextField classNameField;
    private JButton addButton;
    private JLabel messageLabel;
    public ClassAddPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.classService = ClassService.getInstance();
        initComponents();
        layoutComponents();
        addListeners();
    }
    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "添加新班级"));
        classNameField = new JTextField(20);
        addButton = new JButton("添加班级");
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
    }
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM);
// 班级名称标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("班级名称:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(classNameField, gbc);
        // 添加按钮
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(addButton, gbc);
// 消息标签
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);
    }
    private void addListeners() {
        addButton.addActionListener(e -> {
            String className = classNameField.getText().trim();
            if (className.isEmpty()) {
                showMessage("班级名称不能为空", false);
                return;
            }
            try {
                SchoolClass newClass = new SchoolClass(UUID.randomUUID().toString(), className);
                classService.addClass(newClass);
                classNameField.setText("");
                showMessage("班级 " + className + " 添加成功", true);
                mainFrame.refreshAll();
            } catch (Exception ex) {
                showMessage("添加班级失败: " + ex.getMessage(), false);
            }
        });
    }
    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setForeground(isSuccess ? Color.BLUE : Color.RED);
        Timer timer = new Timer(3000, e -> messageLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    public void clearFields() {
        classNameField.setText("");
        messageLabel.setText(" ");
    }
}