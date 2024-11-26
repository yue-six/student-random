package com.student.view;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.service.ClassService;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class GroupListPanel extends JPanel {
    private ClassService classService;
    private SchoolClass currentClass;
    private JList<Group> groupList;
    private DefaultListModel<Group> listModel;
    private JButton deleteButton;
    private JButton detailButton;
    private JLabel totalCountLabel;

    public GroupListPanel(ClassService classService) {
        this.classService = classService;
        initComponents();
        layoutComponents();
        addListeners();

        // 初始化时尝试加载默认班级的小组
        List<SchoolClass> classes = classService.getAllClasses();
        if (classes != null && !classes.isEmpty()) {
            onClassChanged(classes.get(0));
        }
    }

    public void onClassChanged(SchoolClass newClass) {
        System.out.println("GroupListPanel - onClassChanged: " +
                (newClass != null ? newClass.getName() + ", Groups: " + newClass.getGroups().size() : "null"));
        this.currentClass = newClass;
        refreshData();
    }

    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "小组列表"));

        listModel = new DefaultListModel<>();
        groupList = new JList<>(listModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.setCellRenderer(new GroupListCellRenderer());

        deleteButton = new JButton("删除小组");
        detailButton = new JButton("小组详情");
        totalCountLabel = new JLabel("当前无班级");

        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM));

        JScrollPane scrollPane = new JScrollPane(groupList);
        scrollPane.setPreferredSize(new Dimension(Constant.LIST_WIDTH, Constant.LIST_HEIGHT));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(detailButton);
        buttonPanel.add(deleteButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(totalCountLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }

    private void addListeners() {
        groupList.addListSelectionListener(e -> {
            boolean hasSelection = groupList.getSelectedValue() != null;
            deleteButton.setEnabled(hasSelection);
            detailButton.setEnabled(hasSelection);
        });

        deleteButton.addActionListener(e -> {
            Group selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null && currentClass != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "确定要删除小组 " + selectedGroup.getName() + " 吗？\n" +
                                "该操作将解散小组中的所有学生！",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    currentClass.removeGroup(selectedGroup);
                    classService.saveData();
                    refreshData();
                }
            }
        });

        detailButton.addActionListener(e -> {
            Group selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                showGroupDetails(selectedGroup);
            }
        });
    }

    public void refreshData() {
        listModel.clear();
        if (currentClass != null) {
            System.out.println("Refreshing groups for class: " + currentClass.getName());
            System.out.println("Number of groups: " + currentClass.getGroups().size());

            for (Group group : currentClass.getGroups()) {
                System.out.println("Adding group to list: " + group.getName());
                listModel.addElement(group);
            }

            totalCountLabel.setText(currentClass.getName() + " - 共" +
                    currentClass.getGroups().size() + "个小组");
        } else {
            System.out.println("No current class selected");
            totalCountLabel.setText("当前无班级");
        }

        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
    }

    private void showGroupDetails(Group group) {
        StringBuilder details = new StringBuilder();
        details.append("小组名称: ").append(group.getName()).append("\n");
        details.append("小组ID: ").append(group.getId()).append("\n\n");
        details.append("成员数量: ").append(group.getStudents().size()).append("人\n\n");

        if (!group.getStudents().isEmpty()) {
            details.append("成员列表:\n");
            group.getStudents().forEach(student ->
                    details.append("- ").append(student.getName())
                            .append(" (").append(student.getScore()).append("分)\n"));
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(Constant.FONT_NORMAL);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "小组详情",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private class GroupListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Group) {
                Group group = (Group) value;
                setText(group.getName() + " (" + group.getStudents().size() + "人)");
                setFont(Constant.FONT_NORMAL);
            }
            return this;
        }
    }
}