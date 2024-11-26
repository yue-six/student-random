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

public class StudentListPanel extends JPanel {
    private ClassService classService;
    private SchoolClass currentClass;
    private JComboBox<Group> groupComboBox;
    private JList<Student> studentList;
    private DefaultListModel<Student> listModel;
    private JButton deleteButton;
    private JButton detailButton;
    private JButton changeGroupButton;
    private JLabel totalCountLabel;

    public StudentListPanel(ClassService classService) {
        this.classService = classService;
        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "学生列表"));

        groupComboBox = new JComboBox<>();
        groupComboBox.addItem(null);

        listModel = new DefaultListModel<>();
        studentList = new JList<>(listModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setCellRenderer(new StudentListCellRenderer());

        deleteButton = new JButton("删除学生");
        detailButton = new JButton("学生详情");
        changeGroupButton = new JButton("更改小组");
        totalCountLabel = new JLabel("当前无班级");

        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
        changeGroupButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择小组:"));
        topPanel.add(groupComboBox);
        topPanel.add(totalCountLabel);

        JScrollPane scrollPane = new JScrollPane(studentList);
        scrollPane.setPreferredSize(new Dimension(Constant.LIST_WIDTH, Constant.LIST_HEIGHT));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(detailButton);
        buttonPanel.add(changeGroupButton);
        buttonPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        groupComboBox.addActionListener(e -> refreshStudentList());

        studentList.addListSelectionListener(e -> {
            boolean hasSelection = studentList.getSelectedValue() != null;
            deleteButton.setEnabled(hasSelection);
            detailButton.setEnabled(hasSelection);
            changeGroupButton.setEnabled(hasSelection);
        });

        deleteButton.addActionListener(e -> {
            Student selectedStudent = studentList.getSelectedValue();
            if (selectedStudent != null && currentClass != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "确定要删除学生 " + selectedStudent.getName() + " 吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (selectedStudent.getGroupId() != null) {
                        currentClass.getGroups().stream()
                                .filter(g -> g.getId().equals(selectedStudent.getGroupId()))
                                .findFirst()
                                .ifPresent(g -> g.removeStudent(selectedStudent));
                    }
                    currentClass.removeStudent(selectedStudent);
                    classService.saveData();
                    refreshData();
                }
            }
        });

        detailButton.addActionListener(e -> {
            Student selectedStudent = studentList.getSelectedValue();
            if (selectedStudent != null) {
                showStudentDetails(selectedStudent);
            }
        });

        changeGroupButton.addActionListener(e -> {
            Student selectedStudent = studentList.getSelectedValue();
            if (selectedStudent != null && currentClass != null) {
                changeStudentGroup(selectedStudent);
            }
        });
    }

    public void onClassChanged(SchoolClass newClass) {
        this.currentClass = newClass;
        refreshGroupComboBox();
        refreshData();
    }

    private void refreshGroupComboBox() {
        groupComboBox.removeAllItems();
        groupComboBox.addItem(null);
        if (currentClass != null) {
            for (Group group : currentClass.getGroups()) {
                groupComboBox.addItem(group);
            }
        }
    }

    public void refreshData() {
        refreshGroupComboBox();
        refreshStudentList();
    }

    private void refreshStudentList() {
        listModel.clear();
        if (currentClass != null) {
            Group selectedGroup = (Group) groupComboBox.getSelectedItem();
            if (selectedGroup != null) {
                for (Student student : selectedGroup.getStudents()) {
                    listModel.addElement(student);
                }
                totalCountLabel.setText(selectedGroup.getName() + " - " +
                        selectedGroup.getStudents().size() + "人");
            } else {
                for (Student student : currentClass.getStudents()) {
                    listModel.addElement(student);
                }
                totalCountLabel.setText(currentClass.getName() + " - 共" +
                        currentClass.getStudents().size() + "人");
            }
        } else {
            totalCountLabel.setText("当前无班级");
        }

        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
        changeGroupButton.setEnabled(false);
    }

    private void showStudentDetails(Student student) {
        StringBuilder details = new StringBuilder();
        details.append("学生姓名: ").append(student.getName()).append("\n");
        details.append("学号: ").append(student.getId()).append("\n");

        if (student.getGroupId() != null && currentClass != null) {
            currentClass.getGroups().stream()
                    .filter(g -> g.getId().equals(student.getGroupId()))
                    .findFirst()
                    .ifPresent(group ->
                            details.append("所属小组: ").append(group.getName()).append("\n"));
        }

        details.append("\n得分: ").append(student.getScore());

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(Constant.FONT_NORMAL);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "学生详情",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void changeStudentGroup(Student student) {
        if (currentClass == null) return;

        JComboBox<Group> newGroupComboBox = new JComboBox<>();
        newGroupComboBox.addItem(null);
        for (Group group : currentClass.getGroups()) {
            newGroupComboBox.addItem(group);
        }

        if (student.getGroupId() != null) {
            for (int i = 0; i < newGroupComboBox.getItemCount(); i++) {
                Group group = newGroupComboBox.getItemAt(i);
                if (group != null && group.getId().equals(student.getGroupId())) {
                    newGroupComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        int result = JOptionPane.showConfirmDialog(this,
                new Object[] {
                        "选择新小组:",
                        newGroupComboBox
                },
                "更改学生小组",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (student.getGroupId() != null) {
                currentClass.getGroups().stream()
                        .filter(g -> g.getId().equals(student.getGroupId()))
                        .findFirst()
                        .ifPresent(g -> g.removeStudent(student));
            }

            Group newGroup = (Group) newGroupComboBox.getSelectedItem();
            if (newGroup != null) {
                newGroup.addStudent(student);
            }

            classService.saveData();
            refreshData();
        }
    }

    private class StudentListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Student) {
                Student student = (Student) value;
                String groupName = "";
                if (student.getGroupId() != null && currentClass != null) {
                    groupName = currentClass.getGroups().stream()
                            .filter(g -> g.getId().equals(student.getGroupId()))
                            .findFirst()
                            .map(g -> " - " + g.getName())
                            .orElse("");
                }
                setText(student.getName() + groupName +
                        " (得分: " + student.getScore() + ")");
                setFont(Constant.FONT_NORMAL);
            }
            return this;
        }
    }
}