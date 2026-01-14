package cn.edu.zut.collect_school.mock;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SchoolDataImporterV2 {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/selecting_school?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "051210";
    private static final int BATCH_SIZE = 100;

    // JSON 数据对应的 Java 类
    static class University {
        private String name;
        private String province;
        private int ranking;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public int getRanking() { return ranking; }
        public void setRanking(int ranking) { this.ranking = ranking; }
    }

    public static void main(String[] args) {
        // 读取 JSON 文件
        List<University> universities = readJsonFile("G:\\Study\\课设\\大三上课设\\数据库相关\\universities_with_rankings_final.json");

        if (universities != null && !universities.isEmpty()) {
            // 导入数据库
            importToDatabase(universities);
        } else {
            System.out.println("没有读取到数据或数据为空！");
        }
    }

    /**
     * 读取 JSON 文件
     */
    private static List<University> readJsonFile(String filePath) {
        try {
            Gson gson = new Gson();
            List<University> universities = gson.fromJson(
                    new FileReader(filePath),
                    new TypeToken<List<University>>(){}.getType()
            );
            System.out.println("成功读取 " + universities.size() + " 条数据");
            return universities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将数据导入数据库
     */
    private static void importToDatabase(List<University> universities) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "INSERT INTO school (school_name, school_rank, school_address) VALUES (?, ?, ?)";

        try {
            // 1. 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 建立数据库连接
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // 关闭自动提交，使用批处理

            // 3. 创建 PreparedStatement
            pstmt = conn.prepareStatement(sql);

            int count = 0;
            int totalCount = 0;

            // 4. 遍历数据并批量插入
            for (University university : universities) {
                pstmt.setString(1, university.getName());          // school_name
                pstmt.setInt(2, university.getRanking());          // school_rank
                pstmt.setString(3, university.getProvince());      // school_address
                pstmt.addBatch();

                count++;

                // 每 BATCH_SIZE 条执行一次批处理
                if (count % BATCH_SIZE == 0) {
                    int[] results = pstmt.executeBatch();
                    conn.commit();
                    totalCount += results.length;
                    System.out.println("已插入 " + totalCount + " 条记录");
                }
            }

            // 执行剩余的批处理
            if (count % BATCH_SIZE != 0) {
                int[] results = pstmt.executeBatch();
                conn.commit();
                totalCount += results.length;
            }

            System.out.println("数据导入完成！总共插入 " + totalCount + " 条记录");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC 驱动未找到！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("数据库操作出错！");
            e.printStackTrace();

            // 出错时回滚事务
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("事务已回滚");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // 5. 关闭资源
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}