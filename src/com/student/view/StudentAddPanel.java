package com.student.view;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.entity.Student;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.UUID;

public class StudentAddPanel extends JPanel {
    private ClassService classService;
    private MainFrame mainFrame;
    private JComboBox<SchoolClass> classComboBox;
    private JComboBox<Group> groupComboBox;
    private JTextField studentNameField;
    private JTextField studentIdField;
    private JButton addButton;
    private JLabel messageLabel;

    public StudentAddPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.classService = ClassService.getInstance();
        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "添加新学生"));

        classComboBox = new JComboBox<>();
        groupComboBox = new JComboBox<>();
        studentNameField = new JTextField(20);
        studentIdField = new JTextField(20);
        addButton = new JButton("添加学生");
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);

        refreshClassComboBox();
        groupComboBox.addItem(null);
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM,
                Constant.PADDING_MEDIUM);

        int row = 0;

        // 班级选择
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("选择班级:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(classComboBox, gbc);

        // 小组选择
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("选择小组:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        add(groupComboBox, gbc);

        // 学生姓名
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("学生姓名:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        add(studentNameField, gbc);

        // 学号
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("学号:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.WEST;
        add(studentIdField, gbc);

        // 添加按钮
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(addButton, gbc);

        // 消息标签
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(messageLabel, gbc);
    }

    private void addListeners() {
        classComboBox.addActionListener(e -> {
            SchoolClass selectedClass = (SchoolClass) classComboBox.getSelectedItem();
            refreshGroupComboBox(selectedClass);
        });

        addButton.addActionListener(e -> {
            SchoolClass selectedClass = (SchoolClass) classComboBox.getSelectedItem();
            if (selectedClass == null) {
                showMessage("请先选择班级", false);
                return;
            }

            String studentName = studentNameField.getText().trim();
            if (studentName.isEmpty()) {
                showMessage("学生姓名不能为空", false);
                return;
            }

            try {
                String studentId = studentIdField.getText().trim();
                if (studentId.isEmpty()) {
                    studentId = UUID.randomUUID().toString();
                }

                Student newStudent = new Student(studentId, studentName);
                Group selectedGroup = (Group) groupComboBox.getSelectedItem();

                if (selectedGroup != null) {
                    selectedGroup.addStudent(newStudent);
                    newStudent.setGroupId(selectedGroup.getId());
                }

                selectedClass.addStudent(newStudent);
                classService.saveData();

                clearFields();
                showMessage("学生 " + studentName + " 添加成功", true);
                mainFrame.refreshAll();

            } catch (Exception ex) {
                showMessage("添加学生失败: " + ex.getMessage(), false);
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

    private void refreshGroupComboBox(SchoolClass schoolClass) {
        groupComboBox.removeAllItems();
        groupComboBox.addItem(null);
        if (schoolClass != null) {
            for (Group group : schoolClass.getGroups()) {
                groupComboBox.addItem(group);
            }
        }
    }

    public void clearFields() {
        studentNameField.setText("");
        studentIdField.setText("");
        messageLabel.setText(" ");
    }
}