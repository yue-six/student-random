package com.student.service;

import com.student.entity.SchoolClass;
import com.student.entity.Group;
import com.student.entity.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ClassService {
    // 定义班级列表
    private final List<SchoolClass> classes;
    // 单例模式实例
    private static ClassService instance;
    // 数据文件路径
    private static final String DATA_FILE = "classes.dat";
    // 用于生成随机数
    private final Random random;

    // 构造器私有化，防止外部直接实例化
    private ClassService() {
        this.classes = new ArrayList<>();
        this.random = new Random();
        // 在构造器中加载数据
        loadData();
    }

    // 提供公共方法获取单例对象
    public static ClassService getInstance() {
        if (instance == null) {
            instance = new ClassService();
        }
        return instance;
    }

    // 从文件加载班级数据
    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    // 读取文件中的班级列表
                    List<SchoolClass> loadedClasses = (List<SchoolClass>) ois.readObject();
                    // 清空当前列表并添加读取的数据
                    classes.clear();
                    classes.addAll(loadedClasses);
                    // 输出加载信息
                    System.out.println("Loaded " + classes.size() + " classes");
                    for (SchoolClass cls : classes) {
                        System.out.println("Class: " + cls.getName() + ", Groups: " + cls.getGroups().size());
                    }
                }
            }
        } catch (Exception e) {
            // 异常处理，清空列表
            e.printStackTrace();
            classes.clear();
        }
    }

    // 将班级数据保存至文件
    public void saveData() {
        try {
            // 输出保存信息
            System.out.println("Saving data...");
            System.out.println("Number of classes: " + classes.size());
            for (SchoolClass cls : classes) {
                System.out.println("Saving class: " + cls.getName() + ", Groups: " + cls.getGroups().size());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                // 将班级列表写入文件
                oos.writeObject(new ArrayList<>(classes));
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
        }
    }

    // 获取所有班级
    public List<SchoolClass> getAllClasses() {
        System.out.println("Getting all classes, count: " + classes.size());
        // 返回班级列表的副本
        return new ArrayList<>(classes);
    }

    // 添加班级
    public void addClass(SchoolClass schoolClass) {
        if (schoolClass != null) {
            classes.add(schoolClass);
            // 保存更改
            saveData();
        }
    }

    // 移除班级
    public void removeClass(SchoolClass schoolClass) {
        if (schoolClass != null) {
            classes.remove(schoolClass);
            // 保存更改
            saveData();
        }
    }

    // 根据ID获取班级
    public SchoolClass getClassById(String id) {
        if (id != null) {
            // 查找符合条件的班级
            Optional<SchoolClass> result = classes.stream()
                    .filter(c -> id.equals(c.getId()))
                    .findFirst();
            if (result.isPresent()) {
                SchoolClass foundClass = result.get();
                System.out.println("Found class: " + foundClass.getName());
                return foundClass;
            }
        }
        System.out.println("Class not found for id: " + id);
        return null;
    }

    // 检查班级名称是否存在
    public boolean isClassNameExists(String className) {
        if (className != null) {
            return classes.stream()
                    .anyMatch(c -> className.equals(c.getName()));
        }
        return false;
    }

    // 检查小组名称在特定班级中是否存在
    public boolean isGroupNameExists(String classId, String groupName) {
        if (classId != null && groupName != null) {
            SchoolClass schoolClass = getClassById(classId);
            if (schoolClass != null) {
                return schoolClass.getGroups().stream()
                        .anyMatch(g -> groupName.equals(g.getName()));
            }
        }
        return false;
    }

    // 从特定班级中随机选择一个小组
    public Group getRandomGroup(String classId) {
        SchoolClass schoolClass = getClassById(classId);
        if (schoolClass != null) {
            List<Group> groups = schoolClass.getGroups();
            if (!groups.isEmpty()) {
                int index = random.nextInt(groups.size());
                Group selectedGroup = groups.get(index);
                System.out.println("Randomly selected group: " + selectedGroup.getName());
                return selectedGroup;
            }
        }
        return null;
    }

    // 从特定班级中随机选择一名学生
    public Student getRandomStudent(String classId) {
        SchoolClass schoolClass = getClassById(classId);
        if (schoolClass != null) {
            List<Student> students = schoolClass.getStudents();
            if (!students.isEmpty()) {
                int index = random.nextInt(students.size());
                Student selectedStudent = students.get(index);
                System.out.println("Randomly selected student: " + selectedStudent.getName());
                return selectedStudent;
            }
        }
        return null;
    }

    // 从特定班级的特定小组中随机选择一名学生
    public Student getRandomStudentFromGroup(String classId, String groupId) {
        if (classId != null && groupId != null) {
            SchoolClass schoolClass = getClassById(classId);
            if (schoolClass != null) {
                Optional<Group> groupOpt = schoolClass.getGroups().stream()
                        .filter(g -> groupId.equals(g.getId()))
                        .findFirst();
                if (groupOpt.isPresent()) {
                    Group group = groupOpt.get();
                    List<Student> students = group.getStudents();
                    if (!students.isEmpty()) {
                        int index = random.nextInt(students.size());
                        Student selectedStudent = students.get(index);
                        System.out.println("Randomly selected student from group " +
                                group.getName() + ": " + selectedStudent.getName());
                        return selectedStudent;
                    }
                }
            }
        }
        return null;
    }

    // 检查特定班级中是否存在特定的学生ID
    public boolean isStudentIdExists(String classId, String studentId) {
        if (classId != null && studentId != null) {
            SchoolClass schoolClass = getClassById(classId);
            if (schoolClass != null) {
                return schoolClass.getStudents().stream()
                        .anyMatch(s -> studentId.equals(s.getId()));
            }
        }
        return false;
    }
}
