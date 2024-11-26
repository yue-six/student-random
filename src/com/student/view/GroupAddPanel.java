package com.student.view;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.UUID;

public class GroupAddPanel extends JPanel {
    private ClassService classService;
    private MainFrame mainFrame;
    private JComboBox<SchoolClass> classComboBox;
    private JTextField groupNameField;
    private JButton addButton;
    private JLabel messageLabel;

    public GroupAddPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.classService = ClassService.getInstance();
        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "添加新小组"));

        classComboBox = new JComboBox<>();
        groupNameField = new JTextField(20);
        addButton = new JButton("添加小组");
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);

        refreshClassComboBox();
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM);

        // 班级选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("选择班级:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(classComboBox, gbc);

        // 小组名称
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("小组名称:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(groupNameField, gbc);

        // 添加按钮
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(addButton, gbc);

        // 消息标签
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);
    }

    private void addListeners() {
        addButton.addActionListener(e -> {
            SchoolClass selectedClass = (SchoolClass) classComboBox.getSelectedItem();
            if (selectedClass == null) {
                showMessage("请先选择班级", false);
                return;
            }

            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showMessage("小组名称不能为空", false);
                return;
            }

            try {
                // 创建新小组
                Group newGroup = new Group(
                        UUID.randomUUID().toString(),
                        groupName,
                        selectedClass.getId()
                );

                // 添加到班级
                selectedClass.addGroup(newGroup);

                // 保存数据
                classService.saveData();

                // 清空输入框
                groupNameField.setText("");

                // 显示成功消息
                showMessage("小组 " + groupName + " 添加成功", true);

                // 刷新界面
                mainFrame.refreshAll();

            } catch (Exception ex) {
                showMessage("添加小组失败: " + ex.getMessage(), false);
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

    public void refreshClassComboBox() {
        classComboBox.removeAllItems();
        for (SchoolClass schoolClass : classService.getAllClasses()) {
            classComboBox.addItem(schoolClass);
        }
    }

    public void clearFields() {
        groupNameField.setText("");
        messageLabel.setText(" ");
    }
}