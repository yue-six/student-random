package com.student.view;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RandomGroupPanel extends JPanel {
    private ClassService classService;
    private SchoolClass currentClass;
    private JButton randomButton;
    private JLabel resultLabel;
    private Timer blinkTimer;
    private int blinkCount;
    private Group lastSelectedGroup;

    public RandomGroupPanel(ClassService classService) {
        this.classService = classService;
        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "随机抽组"));

        randomButton = new JButton("开始抽取");
        resultLabel = new JLabel("等待抽取...");
        resultLabel.setFont(Constant.FONT_TITLE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        blinkTimer = new Timer(Constant.BLINK_INTERVAL, e -> updateRandomDisplay());
        blinkCount = 0;

        randomButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(resultLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(randomButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        randomButton.addActionListener(e -> {
            if (!blinkTimer.isRunning()) {
                startRandomSelection();
            }
        });
    }

    private void startRandomSelection() {
        if (currentClass == null || currentClass.getGroups().isEmpty()) {
            return;
        }

        blinkCount = 0;
        randomButton.setEnabled(false);
        blinkTimer.start();
    }

    private void updateRandomDisplay() {
        if (currentClass == null) return;

        Group randomGroup = classService.getRandomGroup(currentClass.getId());
        if (randomGroup != null) {
            resultLabel.setText(randomGroup.getName());
        }

        blinkCount++;
        if (blinkCount >= Constant.BLINK_TIMES) {
            blinkTimer.stop();
            lastSelectedGroup = randomGroup;
            randomButton.setEnabled(true);
        }
    }

    public void onClassChanged(SchoolClass newClass) {
        this.currentClass = newClass;
        refreshData();
    }

    public void refreshData() {
        if (currentClass != null) {
            resultLabel.setText("等待抽取...");
            randomButton.setEnabled(!currentClass.getGroups().isEmpty());
        } else {
            resultLabel.setText("请先选择班级");
            randomButton.setEnabled(false);
        }

        if (blinkTimer.isRunning()) {
            blinkTimer.stop();
            blinkCount = 0;
            randomButton.setEnabled(true);
        }
    }
}