package com.student.view;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class MainFrame extends JFrame {
    private ClassService classService;
    private SchoolClass currentClass;
    private JTabbedPane tabbedPane;
    private JPanel statusPanel;
    private JLabel statusLabel;

    // 各个功能面板
    private ClassAddPanel classAddPanel;
    private ClassListPanel classListPanel;
    private GroupAddPanel groupAddPanel;
    private GroupListPanel groupListPanel;
    private StudentAddPanel studentAddPanel;
    private StudentListPanel studentListPanel;
    private RandomGroupPanel randomGroupPanel;
    private RandomStudentPanel randomStudentPanel;
    private ChangeClassPanel changeClassPanel;

    public MainFrame() {
        classService = ClassService.getInstance();
        initComponents();
        layoutComponents();
        setupFrame();
        addListeners();

        // 如果有班级，默认选择第一个班级
        List<SchoolClass> allClasses = classService.getAllClasses();
        if (allClasses != null && !allClasses.isEmpty()) {
            SchoolClass defaultClass = allClasses.get(0);
            System.out.println("Setting default class: " + defaultClass.getName());
            onClassChanged(defaultClass);
        }
    }

    private void initComponents() {
        // 初始化主要组件
        tabbedPane = new JTabbedPane();
        statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 初始化所有面板
        classAddPanel = new ClassAddPanel(this);
        classListPanel = new ClassListPanel(classService);
        groupAddPanel = new GroupAddPanel(this);
        groupListPanel = new GroupListPanel(classService);
        studentAddPanel = new StudentAddPanel(this);
        studentListPanel = new StudentListPanel(classService);
        randomGroupPanel = new RandomGroupPanel(classService);
        randomStudentPanel = new RandomStudentPanel(classService);
        changeClassPanel = new ChangeClassPanel(this);

        // 设置状态栏
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // 创建菜单
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("退出");

        saveItem.addActionListener(e -> saveAll());
        exitItem.addActionListener(e -> saveAndExit());

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // 创建并添加所有标签页
        tabbedPane.addTab("班级管理", createClassPanel());
        tabbedPane.addTab("小组管理", createGroupPanel());
        tabbedPane.addTab("学生管理", createStudentPanel());
        tabbedPane.addTab("随机抽组", randomGroupPanel);
        tabbedPane.addTab("随机点名", randomStudentPanel);
        tabbedPane.addTab("切换班级", changeClassPanel);

        // 添加主面板和状态栏
        add(tabbedPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createClassPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(classAddPanel, BorderLayout.NORTH);
        panel.add(classListPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(groupAddPanel, BorderLayout.NORTH);
        panel.add(groupListPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(studentAddPanel, BorderLayout.NORTH);
        panel.add(studentListPanel, BorderLayout.CENTER);
        return panel;
    }

    private void setupFrame() {
        setTitle("学生管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        // 初始时启用所有标签页
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, true);
        }
    }

    private void addListeners() {
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            System.out.println("Tab changed to: " + selectedIndex); // 调试信息

            // 如果切换到小组管理标签页，刷新小组列表
            if (selectedIndex == 1 && currentClass != null) {
                groupListPanel.refreshData();
            }

            updateStatusByTab(selectedIndex);
        });

    }

    private void updateStatusByTab(int tabIndex) {
        if (currentClass == null && tabIndex > 0 && tabIndex < 5) {
            statusLabel.setText("请先选择班级");
            return;
        }

        switch (tabIndex) {
            case 0:
                statusLabel.setText("班级管理");
                break;
            case 1:
                statusLabel.setText("小组管理 - " +
                        (currentClass != null ? currentClass.getName() : ""));
                break;
            case 2:
                statusLabel.setText("学生管理 - " +
                        (currentClass != null ? currentClass.getName() : ""));
                break;
            case 3:
                statusLabel.setText("随机抽组 - " +
                        (currentClass != null ? currentClass.getName() : ""));
                break;
            case 4:
                statusLabel.setText("随机点名 - " +
                        (currentClass != null ? currentClass.getName() : ""));
                break;
            case 5:
                statusLabel.setText("切换班级");
                break;
        }
    }

    public void onClassChanged(SchoolClass newClass) {
        System.out.println("MainFrame - Changing class to: " +
                (newClass != null ? newClass.getName() : "null")); // 调试信息

        this.currentClass = newClass;

        // 更新各个面板的状态
        groupAddPanel.refreshClassComboBox();
        groupListPanel.onClassChanged(newClass);
        studentAddPanel.refreshClassComboBox();
        studentListPanel.onClassChanged(newClass);
        randomGroupPanel.onClassChanged(newClass);
        randomStudentPanel.onClassChanged(newClass);
        changeClassPanel.updateCurrentClassLabel(newClass);

        // 更新状态栏
        updateStatusByTab(tabbedPane.getSelectedIndex());

        System.out.println("Class change completed"); // 调试信息
    }

    private void saveAll() {
        try {
            updateStatus("正在保存数据...");
            classService.saveData();
            updateStatus("数据保存成功");
        } catch (Exception e) {
            handleError("保存数据失败", e);
        }
    }

    private void saveAndExit() {
        saveAll();
        dispose();
        System.exit(0);
    }

    public void refreshAll() {
        System.out.println("MainFrame - refreshAll called");
        classListPanel.refreshData();
        if (currentClass != null) {
            System.out.println("Refreshing data for class: " + currentClass.getName());
            groupListPanel.refreshData();
            studentListPanel.refreshData();
            randomGroupPanel.refreshData();
            randomStudentPanel.refreshData();
        }
        groupAddPanel.refreshClassComboBox();
        studentAddPanel.refreshClassComboBox();
        changeClassPanel.refreshClassList();
        updateStatusByTab(tabbedPane.getSelectedIndex());
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "学生管理系统 v1.0\n" +
                        "作者：Your Name\n" +
                        "版权所有 © " + java.time.Year.now().getValue(),
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\n错误信息：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        updateStatus("发生错误");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    // Getter方法
    public SchoolClass getCurrentClass() {
        return currentClass;
    }

    public ClassService getClassService() {
        return classService;
    }
}