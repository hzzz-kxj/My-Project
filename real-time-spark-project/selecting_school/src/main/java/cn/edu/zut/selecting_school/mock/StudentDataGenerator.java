package cn.edu.zut.collect_school.mock;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StudentDataGenerator {
    // HDFS配置
    private static final String HDFS_URI = "hdfs://hadoop102:8020";
    private static final String MAJOR_FILE_PATH = "/origin_data/major/major.csv";
    private static final String OUTPUT_PATH = "/origin_data/student/student.csv";
    private static final int RECORDS_PER_SECOND = 10; // 每秒增加的数据条数

    // 学历分布权重（本科:硕士:博士 = 7:2:1）
    private static final String[] EDUCATION_LEVELS = {"本科", "硕士", "博士"};
    private static final double[] EDUCATION_WEIGHTS = {0.7, 0.2, 0.1};

    // 家庭水平分布权重（贫穷:一般:小康:富有 = 1:5:3:1）
    private static final String[] FAMILY_LEVELS = {"贫穷", "一般", "小康", "富有"};
    private static final double[] FAMILY_WEIGHTS = {0.1, 0.5, 0.3, 0.1};

    // 不同家庭水平对应的收入范围
    private static final int[][] INCOME_RANGES = {
            {3000, 8000},   // 贫穷
            {8001, 20000},  // 一般
            {20001, 35000}, // 小康
            {35001, 50000}  // 富有
    };

    // 省份地址列表
    private static final String[] PROVINCES = {
            "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "江苏省", "浙江省",
            "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省",
            "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省",
            "青海省", "台湾省", "内蒙古", "广西省", "宁夏省", "西藏省", "新疆省",
            "北京市", "天津市", "上海市", "重庆市", "香港", "澳门"
    };

    private static final Random random = new Random();
    private static List<MajorInfo> majorInfos; // 存储专业信息的列表
    private static FileSystem fs; // HDFS文件系统对象

    public static void main(String[] args) {
        System.out.println("学生数据实时生成器启动...");
        System.out.println("HDFS URI: " + HDFS_URI);
        System.out.println("专业数据文件: " + MAJOR_FILE_PATH);
        System.out.println("输出文件: " + OUTPUT_PATH);
        System.out.println("数据生成速率: " + RECORDS_PER_SECOND + " 条/秒");
        System.out.println("按 Ctrl+C 终止程序\n");

        try {
            // 初始化HDFS连接
            initHDFS();

            // 加载专业信息
            majorInfos = loadMajorInfoFromHDFS();
            if (majorInfos.isEmpty()) {
                System.err.println("错误: 未加载到专业数据，请检查文件: " + MAJOR_FILE_PATH);
                return;
            }
            System.out.println("成功加载 " + majorInfos.size() + " 条专业信息");

            // 检查文件是否存在，如果不存在则创建并写入表头
            initializeFileInHDFS();

            // 获取当前最大的ID
            int lastId = getLastIdFromHDFS();
            System.out.println("当前最后一条数据的ID: " + lastId);

            // 开始实时生成数据
            realTimeGenerateDataToHDFS(lastId);

        } catch (Exception e) {
            System.err.println("初始化失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeHDFS();
        }
    }

    /**
     * 初始化HDFS连接
     */
    private static void initHDFS() throws Exception {
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.set("fs.defaultFS", HDFS_URI);

        fs = FileSystem.get(new URI(HDFS_URI), conf);
        System.out.println("HDFS连接成功！");
    }

    /**
     * 关闭HDFS连接
     */
    private static void closeHDFS() {
        try {
            if (fs != null) {
                fs.close();
                System.out.println("HDFS连接已关闭");
            }
        } catch (IOException e) {
            System.err.println("关闭HDFS连接失败: " + e.getMessage());
        }
    }

    /**
     * 从HDFS加载专业信息
     */
    private static List<MajorInfo> loadMajorInfoFromHDFS() throws IOException {
        List<MajorInfo> infos = new ArrayList<>();
        Path majorPath = new Path(MAJOR_FILE_PATH);

        if (!fs.exists(majorPath)) {
            System.err.println("警告: 专业数据文件不存在: " + MAJOR_FILE_PATH);
            return infos;
        }

        try (FSDataInputStream inputStream = fs.open(majorPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // 跳过表头
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 跳过空行
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 解析CSV行，格式应为: major_id,major_name,school_name,...
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        int majorId = Integer.parseInt(parts[0].trim());
                        String schoolName = parts[2].trim();
                        infos.add(new MajorInfo(majorId, schoolName));
                    } catch (NumberFormatException e) {
                        System.err.println("警告: 跳过无效的专业ID: " + parts[0]);
                    }
                }
            }
        }

        return infos;
    }

    /**
     * 在HDFS中初始化文件
     */
    private static void initializeFileInHDFS() throws IOException {
        Path outputPath = new Path(OUTPUT_PATH);

        if (!fs.exists(outputPath)) {
            System.out.println("文件不存在，创建新文件并写入表头...");
            try (FSDataOutputStream outputStream = fs.create(outputPath)) {
                String header = "student_id,major_id,student_school,student_education,student_family_level,student_income,student_address\n";
                outputStream.write(header.getBytes());
                System.out.println("文件创建成功，表头已写入");
            }
        } else {
            System.out.println("文件已存在，将追加数据");
        }
    }

    /**
     * 从HDFS获取文件中最后一条数据的ID
     */
    private static int getLastIdFromHDFS() throws IOException {
        Path outputPath = new Path(OUTPUT_PATH);

        if (!fs.exists(outputPath)) {
            return 0; // 文件不存在，从0开始
        }

        try (FSDataInputStream inputStream = fs.open(outputPath)) {
            // 读取文件最后1KB数据来查找最后一行
            long fileSize = fs.getFileStatus(outputPath).getLen();
            long startPos = Math.max(0, fileSize - 1024); // 读取最后1KB

            inputStream.seek(startPos);

            // 读取数据
            byte[] buffer = new byte[(int) (fileSize - startPos)];
            inputStream.readFully(buffer);
            String content = new String(buffer);

            // 查找最后一行
            String[] lines = content.split("\n");
            String lastLine = "";

            for (int i = lines.length - 1; i >= 0; i--) {
                if (!lines[i].trim().isEmpty() && !lines[i].startsWith("student_id,")) {
                    lastLine = lines[i];
                    break;
                }
            }

            if (lastLine.isEmpty()) {
                // 如果没有找到数据行，可能需要读取整个文件
                return getLastIdByFullScan(outputPath);
            }

            // 解析ID
            String[] parts = lastLine.split(",", 2);
            if (parts.length > 0) {
                try {
                    return Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        } catch (Exception e) {
            System.err.println("读取最后一行失败，尝试完整扫描: " + e.getMessage());
            return getLastIdByFullScan(outputPath);
        }

        return 0;
    }

    /**
     * 完整扫描文件获取最后一行ID
     */
    private static int getLastIdByFullScan(Path filePath) throws IOException {
        try (FSDataInputStream inputStream = fs.open(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            String lastLine = "";

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("student_id,") && !line.trim().isEmpty()) {
                    lastLine = line;
                }
            }

            if (lastLine.isEmpty()) {
                return 0;
            }

            String[] parts = lastLine.split(",", 2);
            if (parts.length > 0) {
                try {
                    return Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * 实时生成数据到HDFS
     */
    private static void realTimeGenerateDataToHDFS(int startId) {
        int currentId = startId;
        int secondsCounter = 0;

        // 使用本地缓冲区，减少HDFS写入次数
        StringBuilder buffer = new StringBuilder();
        int bufferCount = 0;
        final int BUFFER_SIZE = 100; // 每100条数据写入一次HDFS

        while (true) {
            long startTime = System.currentTimeMillis();

            // 生成当前秒的数据
            for (int i = 0; i < RECORDS_PER_SECOND; i++) {
                currentId++;

                // 从专业信息列表中随机选择一个专业
                MajorInfo major = majorInfos.get(random.nextInt(majorInfos.size()));

                // 生成其他数据
                String education = getRandomEducationLevel();
                String familyLevel = getRandomFamilyLevel();
                int income = generateIncome(familyLevel);
                String address = getRandomProvince();

                // 添加到缓冲区
                String row = String.format("%d,%d,%s,%s,%s,%d,%s\n",
                        currentId,
                        major.getMajorId(),
                        major.getSchoolName(),
                        education,
                        familyLevel,
                        income,
                        address);

                buffer.append(row);
                bufferCount++;

                // 缓冲区达到指定大小，写入HDFS
                if (bufferCount >= BUFFER_SIZE) {
                    writeBufferToHDFS(buffer.toString());
                    buffer.setLength(0);
                    bufferCount = 0;
                }
            }

            // 确保所有数据都已写入HDFS
            if (bufferCount > 0) {
                writeBufferToHDFS(buffer.toString());
                buffer.setLength(0);
                bufferCount = 0;
            }

            secondsCounter++;

            // 每10秒显示一次统计信息
            if (secondsCounter % 10 == 0) {
                System.out.printf("已生成 %d 秒数据，总记录数: %d\n",
                        secondsCounter * RECORDS_PER_SECOND, currentId);
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // 如果生成数据花费的时间小于1秒，则等待剩余时间
            if (elapsedTime < 1000) {
                try {
                    long sleepTime = 1000 - elapsedTime;
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println("线程休眠被中断: " + e.getMessage());
                    break;
                }
            }
            // 如果生成数据花费的时间超过1秒，显示警告
            else {
                System.out.printf("警告: 生成 %d 条数据花费了 %.1f 秒\n",
                        RECORDS_PER_SECOND, elapsedTime / 1000.0);
            }
        }
    }

    /**
     * 将缓冲区数据写入HDFS
     */
    private static void writeBufferToHDFS(String data) {
        Path outputPath = new Path(OUTPUT_PATH);

        try {
            // 使用追加模式写入数据
            try (FSDataOutputStream outputStream = fs.append(outputPath)) {
                outputStream.write(data.getBytes());
                outputStream.hflush(); // 确保数据写入
            }
        } catch (IOException e) {
            System.err.println("写入HDFS失败: " + e.getMessage());
            // 尝试重新连接HDFS
            try {
                closeHDFS();
                initHDFS();
                // 重新尝试写入
                try (FSDataOutputStream outputStream = fs.append(outputPath)) {
                    outputStream.write(data.getBytes());
                    outputStream.hflush();
                    System.out.println("重新连接HDFS并写入成功");
                }
            } catch (Exception ex) {
                System.err.println("重新连接HDFS失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * 随机生成学历（按权重分布）
     */
    private static String getRandomEducationLevel() {
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;

        for (int i = 0; i < EDUCATION_WEIGHTS.length; i++) {
            cumulativeWeight += EDUCATION_WEIGHTS[i];
            if (randomValue <= cumulativeWeight) {
                return EDUCATION_LEVELS[i];
            }
        }

        return EDUCATION_LEVELS[0]; // 默认返回本科
    }

    /**
     * 随机生成家庭水平（按权重分布）
     */
    private static String getRandomFamilyLevel() {
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;

        for (int i = 0; i < FAMILY_WEIGHTS.length; i++) {
            cumulativeWeight += FAMILY_WEIGHTS[i];
            if (randomValue <= cumulativeWeight) {
                return FAMILY_LEVELS[i];
            }
        }

        return FAMILY_LEVELS[1]; // 默认返回一般
    }

    /**
     * 根据家庭水平生成收入
     */
    private static int generateIncome(String familyLevel) {
        int index = -1;
        for (int i = 0; i < FAMILY_LEVELS.length; i++) {
            if (FAMILY_LEVELS[i].equals(familyLevel)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            index = 1; // 默认"一般"水平
        }

        int minIncome = INCOME_RANGES[index][0];
        int maxIncome = INCOME_RANGES[index][1];

        // 生成正态分布的随机收入（偏向中间值）
        double mean = (minIncome + maxIncome) / 2.0;
        double stdDev = (maxIncome - minIncome) / 6.0;

        double income;
        do {
            income = random.nextGaussian() * stdDev + mean;
        } while (income < minIncome || income > maxIncome);

        return (int) income;
    }

    /**
     * 随机生成省份地址
     */
    private static String getRandomProvince() {
        return PROVINCES[random.nextInt(PROVINCES.length)];
    }

    /**
     * 专业信息类
     */
    static class MajorInfo {
        private int majorId;
        private String schoolName;

        public MajorInfo(int majorId, String schoolName) {
            this.majorId = majorId;
            this.schoolName = schoolName;
        }

        public int getMajorId() {
            return majorId;
        }

        public String getSchoolName() {
            return schoolName;
        }
    }
}