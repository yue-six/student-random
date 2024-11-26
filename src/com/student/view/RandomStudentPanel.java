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
import java.util.HashSet;
import java.util.Set;

public class RandomStudentPanel extends JPanel {
    private ClassService classService;
    private SchoolClass currentClass;
    private JComboBox<Group> groupComboBox;
    private JButton randomButton;
    private JButton addScoreButton;
    private JButton minusScoreButton;
    private JLabel resultLabel;
    private Timer blinkTimer;
    private int blinkCount;
    private Student lastSelectedStudent;
    private Set<Student> excludedStudents;

    public RandomStudentPanel(ClassService classService) {
        this.classService = classService;
        this.excludedStudents = new HashSet<>();
        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "随机点名"));

        groupComboBox = new JComboBox<>();
        groupComboBox.addItem(null);

        randomButton = new JButton("开始抽取");
        addScoreButton = new JButton("+分");
        minusScoreButton = new JButton("-分");

        resultLabel = new JLabel("等待抽取...");
        resultLabel.setFont(Constant.FONT_TITLE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        blinkTimer = new Timer(Constant.BLINK_INTERVAL, e -> updateRandomDisplay());
        blinkCount = 0;

        randomButton.setEnabled(false);
        addScoreButton.setEnabled(false);
        minusScoreButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("选择小组:"));
        topPanel.add(groupComboBox);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(resultLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(randomButton);
        buttonPanel.add(addScoreButton);
        buttonPanel.add(minusScoreButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        groupComboBox.addActionListener(e -> refreshData());

        randomButton.addActionListener(e -> {
            if (!blinkTimer.isRunning()) {
                startRandomSelection();
            }
        });

        addScoreButton.addActionListener(e -> {
            if (lastSelectedStudent != null) {
                lastSelectedStudent.setScore(lastSelectedStudent.getScore() + 1);
                classService.saveData();
                updateResultLabel();
            }
        });

        minusScoreButton.addActionListener(e -> {
            if (lastSelectedStudent != null) {
                lastSelectedStudent.setScore(lastSelectedStudent.getScore() - 1);
                classService.saveData();
                updateResultLabel();
            }
        });
    }

    private void startRandomSelection() {
        if (currentClass == null) return;

        Group selectedGroup = (Group) groupComboBox.getSelectedItem();
        if (selectedGroup != null && selectedGroup.getStudents().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "该小组没有学生",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (currentClass.getStudents().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "班级中没有学生",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        blinkCount = 0;
        randomButton.setEnabled(false);
        addScoreButton.setEnabled(false);
        minusScoreButton.setEnabled(false);
        blinkTimer.start();
    }

    private void updateRandomDisplay() {
        if (currentClass == null) return;

        Student randomStudent;
        Group selectedGroup = (Group) groupComboBox.getSelectedItem();

        if (selectedGroup != null) {
            randomStudent = classService.getRandomStudentFromGroup(
                    currentClass.getId(), selectedGroup.getId());
        } else {
            randomStudent = classService.getRandomStudent(currentClass.getId());
        }

        if (randomStudent != null) {
            resultLabel.setText(randomStudent.toString());
        }

        blinkCount++;
        if (blinkCount >= Constant.BLINK_TIMES) {
            blinkTimer.stop();
            lastSelectedStudent = randomStudent;
            randomButton.setEnabled(true);
            addScoreButton.setEnabled(true);
            minusScoreButton.setEnabled(true);
            excludedStudents.add(randomStudent);
        }
    }

    private void updateResultLabel() {
        if (lastSelectedStudent != null) {
            resultLabel.setText(lastSelectedStudent.toString());
        }
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
        if (currentClass != null) {
            Group selectedGroup = (Group) groupComboBox.getSelectedItem();
            String groupInfo = selectedGroup != null ? " - " + selectedGroup.getName() : "";
            resultLabel.setText("当前班级：" + currentClass.getName() + groupInfo + " - 等待抽取...");
            randomButton.setEnabled(true);
        } else {
            resultLabel.setText("等待抽取...");
            randomButton.setEnabled(false);
        }

        if (blinkTimer.isRunning()) {
            blinkTimer.stop();
            blinkCount = 0;
            randomButton.setEnabled(true);
        }

        addScoreButton.setEnabled(false);
        minusScoreButton.setEnabled(false);
        excludedStudents.clear();
    }
}