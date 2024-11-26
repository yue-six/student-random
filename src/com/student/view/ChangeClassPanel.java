package com.student.view;

import com.student.entity.SchoolClass;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ChangeClassPanel extends JPanel {
    private ClassService classService; // 用于处理班级数据的服务类
    private MainFrame mainFrame; // 主界面框架引用，用于通知主界面班级选择的变化
    private JList<SchoolClass> classList; // 显示所有班级的列表
    private DefaultListModel<SchoolClass> listModel; // 班级列表的数据模型
    private JButton selectButton; // 选择班级的按钮
    private JLabel currentClassLabel; // 显示当前选中的班级

    /**
     * 构造函数，初始化组件和服务，并设置布局和监听器。
     * @param mainFrame 主框架实例，用于与主界面交互
     */
    public ChangeClassPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.classService = ClassService.getInstance(); // 获取单例模式下的ClassService实例
        initComponents(); // 初始化组件
        layoutComponents(); // 布局组件
        addListeners(); // 添加事件监听器
    }

    //初始化面板上的所有组件。
    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "切换班级")); // 设置边框样式

        listModel = new DefaultListModel<>(); // 创建列表模型
        classList = new JList<>(listModel); // 创建班级列表
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 设置为单选模式
        classList.setCellRenderer(new ClassListCellRenderer()); // 设置列表项渲染器

        selectButton = new JButton("选择班级"); // 创建选择按钮
        currentClassLabel = new JLabel("当前未选择班级"); // 创建显示当前班级的标签
        currentClassLabel.setFont(Constant.FONT_BOLD); // 设置字体加粗

        selectButton.setEnabled(false); // 初始状态下禁用选择按钮
        refreshClassList(); // 刷新班级列表
    }

    /**
     * 布局组件，使用BorderLayout布局管理器。
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM)); // 设置布局及内边距

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 创建顶部面板，左对齐
        topPanel.add(currentClassLabel); // 将当前班级标签添加到顶部面板

        JScrollPane scrollPane = new JScrollPane(classList); // 创建滚动窗格，包含班级列表
        scrollPane.setPreferredSize(new Dimension(Constant.LIST_WIDTH, Constant.LIST_HEIGHT)); // 设置滚动窗格的首选大小

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 创建底部面板，居中对齐
        buttonPanel.add(selectButton); // 将选择按钮添加到底部面板

        add(topPanel, BorderLayout.NORTH); // 将顶部面板添加到北边位置
        add(scrollPane, BorderLayout.CENTER); // 将滚动窗格添加到中间位置
        add(buttonPanel, BorderLayout.SOUTH); // 将底部面板添加到南边位置
    }

    //添加监听器以响应用户操作。
    private void addListeners() {
        classList.addListSelectionListener(e -> { // 监听班级列表的选择变化
            selectButton.setEnabled(classList.getSelectedValue() != null); // 如果选择了班级，则启用选择按钮
        });

        selectButton.addActionListener(e -> { // 监听选择按钮点击事件
            SchoolClass selectedClass = classList.getSelectedValue(); // 获取选中的班级
            if (selectedClass != null) { // 如果有班级被选中
                System.out.println("Selected class: " + selectedClass.getName()); // 输出调试信息
                mainFrame.onClassChanged(selectedClass); // 通知主界面班级已更改
                updateCurrentClassLabel(selectedClass); // 更新当前班级标签
            }
        });
    }

    //从服务获取所有班级并刷新列表。

    public void refreshClassList() {
        listModel.clear(); // 清空列表模型
        java.util.List<SchoolClass> classes = classService.getAllClasses(); // 获取所有班级
        System.out.println("Refreshing class list, total classes: " + classes.size()); // 输出调试信息
        for (SchoolClass schoolClass : classes) { // 遍历班级集合
            listModel.addElement(schoolClass); // 将班级添加到列表模型
        }
        selectButton.setEnabled(false); // 没有班级被选中时禁用选择按钮
    }

    //更新当前班级标签的内容。
    public void updateCurrentClassLabel(SchoolClass currentClass) {
        if (currentClass != null) { // 如果有班级被选中
            currentClassLabel.setText("当前班级: " + currentClass.getName()); // 更新标签文本
        } else {
            currentClassLabel.setText("当前未选择班级"); // 如果没有班级被选中，显示默认文本
        }
    }


    //  自定义列表项渲染器，用于在列表中显示班级名称、学生数和组数。

    private class ClassListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); // 调用父类方法

            if (value instanceof SchoolClass) { // 如果值是SchoolClass类型
                SchoolClass schoolClass = (SchoolClass) value;
                setText(schoolClass.getName() + " (" + // 设置列表项文本
                        schoolClass.getStudents().size() + "人, " +
                        schoolClass.getGroups().size() + "组)");
                setFont(Constant.FONT_NORMAL); // 设置字体
            }
            return this; // 返回此组件
        }
    }
}
