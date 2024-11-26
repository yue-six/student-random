package com.student.view;
import com.student.entity.SchoolClass;
import com.student.service.ClassService;
import com.student.util.Constant;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
public class ClassListPanel extends JPanel {
    private ClassService classService;
    private JList<SchoolClass> classList;
    private DefaultListModel<SchoolClass> listModel;
    private JButton deleteButton;
    private JButton detailButton;
    private JLabel totalCountLabel;
    public ClassListPanel(ClassService classService) {
        this.classService = classService;
        initComponents();
        layoutComponents();
        addListeners();
    }
    private void initComponents() {
        setBorder(new TitledBorder(new EtchedBorder(), "班级列表"));
        listModel = new DefaultListModel<>();
        classList = new JList<>(listModel);
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.setCellRenderer(new ClassListCellRenderer());
        deleteButton = new JButton("删除班级");
        detailButton = new JButton("班级详情");
        totalCountLabel = new JLabel("共有 0 个班级");
        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
        refreshData();
    }
    private void layoutComponents() {
        setLayout(new BorderLayout(Constant.PADDING_MEDIUM, Constant.PADDING_MEDIUM));
        JScrollPane scrollPane = new JScrollPane(classList);
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
        classList.addListSelectionListener(e -> {
            boolean hasSelection = classList.getSelectedValue() != null;
            deleteButton.setEnabled(hasSelection);
            detailButton.setEnabled(hasSelection);
        });
        deleteButton.addActionListener(e -> {
            SchoolClass selectedClass = classList.getSelectedValue();
            if (selectedClass != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "确定要删除班级 " + selectedClass.getName() + " 吗？\n" +
                                "该操作将同时删除班级中的所有小组和学生！",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    classService.removeClass(selectedClass);
                    refreshData();
                }
            }
        });
        detailButton.addActionListener(e -> {
            SchoolClass selectedClass = classList.getSelectedValue();
            if (selectedClass != null) {
                showClassDetails(selectedClass);
            }
        });
    }
    public void refreshData() {
        listModel.clear();
        for (SchoolClass schoolClass : classService.getAllClasses()) {
            listModel.addElement(schoolClass);
        }
        updateTotalCount();
        deleteButton.setEnabled(false);
        detailButton.setEnabled(false);
    }
    private void updateTotalCount() {
        int total = classService.getAllClasses().size();
        totalCountLabel.setText("共有 " + total + " 个班级");
    }
    private void showClassDetails(SchoolClass schoolClass) {
        StringBuilder details = new StringBuilder();
        details.append("班级名称: ").append(schoolClass.getName()).append("\n");
        details.append("班级ID: ").append(schoolClass.getId()).append("\n\n");
        details.append("学生总数: ").append(schoolClass.getStudents().size()).append("\n");
        details.append("小组总数: ").append(schoolClass.getGroups().size()).append("\n\n");
        if (!schoolClass.getGroups().isEmpty()) {
            details.append("小组信息:\n");
            schoolClass.getGroups().forEach(group -> {
                details.append("- ").append(group.getName())
                        .append(" (").append(group.getStudents().size())
                        .append("人)\n");
            });
            details.append("\n");
        }
        if (!schoolClass.getStudents().isEmpty()) {
            details.append("学生列表:\n");
            schoolClass.getStudents().forEach(student -> {
                details.append("- ").append(student.getName());
                if (student.getGroupId() != null) {
                    schoolClass.getGroups().stream()
                            .filter(g -> g.getId().equals(student.getGroupId()))
                            .findFirst()
                            .ifPresent(group ->
                                    details.append(" (").append(group.getName()).append(")"));
                }
                details.append("\n");
            });
        }
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(Constant.FONT_NORMAL);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this,
                scrollPane,
                "班级详情",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private class ClassListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof SchoolClass) {
                SchoolClass schoolClass = (SchoolClass) value;
                setText(schoolClass.getName() + " (" +
                        schoolClass.getStudents().size() + "人)");
                setFont(Constant.FONT_NORMAL);
            }
            return this;
        }
    }
}