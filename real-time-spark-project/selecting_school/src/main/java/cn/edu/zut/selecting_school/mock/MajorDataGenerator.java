package cn.edu.zut.collect_school.mock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class MajorDataGenerator {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/selecting_school?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "051210";

    private static final String[] MAJOR_CLASSES = {
            "计算机类", "电子信息类", "机械类", "临床医学类", "经济学类",
            "工商管理类", "中国语言文学类", "数学类", "化学类", "土木类",
            "法学类", "教育学类", "新闻传播学类", "材料类", "环境科学与工程类",
            "药学类", "航空航天类", "外国语言文学类", "自动化类", "农林经济管理类"
    };

    public static void main(String[] args) {
        // 1. 读取JSON文件
        String jsonFilePath = "G:\\Study\\课设\\大三上课设\\数据库相关\\university.json";
        List<School> schools = readSchoolsFromJson(jsonFilePath);
        if (schools == null || schools.isEmpty()) {
            System.out.println("读取学校数据失败！");
            return;
        }

        System.out.println("成功读取 " + schools.size() + " 所学校数据");

        // 2. 连接数据库
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);
            System.out.println("数据库连接成功！");

            // 3. 清空现有数据
            clearExistingData(connection);

            // 4. 生成所有数据（每个专业类内部独立排名）
            System.out.println("开始生成所有专业数据（每个专业类独立排名1-" + schools.size() + "）...");
            int totalRecords = generateAllMajorDataWithIndependentRanks(connection, schools);

            connection.commit();
            System.out.println("\n所有专业数据生成完成！总共生成 " + totalRecords + " 条记录");

        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void clearExistingData(Connection connection) throws SQLException {
        // 先禁用外键约束
        try (PreparedStatement stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
            stmt.execute();
        }

        String deleteQuery = "DELETE FROM major";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            int deletedRows = preparedStatement.executeUpdate();
            System.out.println("已清空 " + deletedRows + " 条现有数据");
        }

        // 重新启用外键约束
        try (PreparedStatement stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
            stmt.execute();
        }
    }

    /**
     * 为每个专业类生成独立排名（每个专业类都有1-1805的排名）
     */
    private static int generateAllMajorDataWithIndependentRanks(Connection connection, List<School> schools) throws SQLException {
        String insertQuery = "INSERT INTO major (major_name, school_name, major_rank) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            int totalRecords = 0;

            for (String majorClass : MAJOR_CLASSES) {
                System.out.print("正在生成专业类: " + majorClass + " ... ");

                // 为这个专业类生成1-schools.size()的随机排名
                List<Integer> ranks = generateRandomRanking(schools.size());

                int batchCount = 0;
                for (int i = 0; i < schools.size(); i++) {
                    School school = schools.get(i);

                    preparedStatement.setString(1, majorClass);
                    preparedStatement.setString(2, school.getName());
                    preparedStatement.setInt(3, ranks.get(i));

                    preparedStatement.addBatch();
                    batchCount++;
                    totalRecords++;

                    // 每500条执行一次批量插入
                    if (batchCount % 500 == 0) {
                        preparedStatement.executeBatch();
                    }
                }

                // 执行这个专业类的剩余批次
                preparedStatement.executeBatch();
                System.out.println("完成 (" + schools.size() + " 条记录)");
            }

            return totalRecords;
        }
    }

    /**
     * 生成1到n的随机排名
     */
    private static List<Integer> generateRandomRanking(int n) {
        List<Integer> ranks = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            ranks.add(i);
        }
        Collections.shuffle(ranks);
        return ranks;
    }

    private static List<School> readSchoolsFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<List<School>>(){}.getType());
        } catch (Exception e) {
            System.err.println("读取JSON文件失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    static class School {
        private String name;
        private String province;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }
}