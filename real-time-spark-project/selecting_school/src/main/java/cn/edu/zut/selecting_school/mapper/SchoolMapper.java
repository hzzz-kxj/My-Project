package cn.edu.zut.collect_school.mapper;

import cn.edu.zut.collect_school.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SchoolMapper {

    /**
     * SparkStreaming实时查询语句
     */
    // 学校排名top10
    @Select("select school_name, school_rank from dws_school_rank_top10 order by school_rank limit 10")
    public List<SchoolRankTop10> schoolRankTop10();

    // 学生所在省份与学校所在省份比例
    @Select("select t1.yes_or_no, round((t1.sum/t2.sum),3) rate from\n" +
            "(select yes_or_no, sum(rate) sum   from dws_province_rate group by yes_or_no) t1,\n" +
            "(select sum(rate) sum from dws_province_rate) t2;")
    public List<ProvinceRate> provinceRate();

    // 学生学历分布
    @Select("select student_education, sum(count) count  from dws_student_education_level group by student_education")
    public List<StudentEducationLevel> studentEducationLevel();

    // 所有专业学生平均收入top10
    @Select("select t1.major_name, round((t1.sum/t2.count),3) avg_income from\n" +
            "(select major_name, sum(avg_income) sum   from dws_major_income_top10 group by major_name) t1,\n" +
            "(select major_name, count(avg_income) count from dws_major_income_top10 group by major_name) t2\n" +
            "where t1.major_name = t2.major_name\n" +
            "order by avg_income desc limit 10;")
    public List<MajorIncomeTop10> majorIncomeTop10();

    // 每个学校学生平均收入top10
    @Select("select t1.school_name, round((t1.sum/t2.count),3) avg_income from\n" +
            "(select school_name, sum(avg_income) sum   from dws_school_student_income_top10 group by school_name) t1,\n" +
            "(select school_name, count(avg_income) count from dws_school_student_income_top10 group by school_name) t2\n" +
            "where t1.school_name = t2.school_name\n" +
            "order by avg_income desc limit 10;")
    public List<SchoolStudentIncomeTop10> schoolStudentIncomeTop10();

    // 学生选择什么专业的人数多top10
    @Select("select major_name, sum(count) count\n" +
            "from dws_major_number\n" +
            "group by major_name\n" +
            "order by count desc limit 10;")
    public List<MajorNumber> majorNumber();

    // 选择去哪上学数量top10
    @Select("select school_address, sum(count) count\n" +
            "from dws_province_number\n" +
            "group by school_address\n" +
            "order by count desc limit 10;")
    public List<ProvinceNumber> provinceNumber();

    // 不同地区学校数量top10
    @Select("select school_address, sum(count) count\n" +
            "from dws_school_address_number\n" +
            "group by school_address\n" +
            "order by count desc limit 10;")
    public List<SchoolAddressNumber> schoolAddressNumber();

    // 学生选择学校更倾向于专业排名还是学校排名
    @Select("select t1.major_or_rank, round((t1.sum/t2.sum),2)*100 preference_count from\n" +
            "(select major_or_rank, sum(preference_count) sum   from dws_major_or_school_rank_preference group by major_or_rank) t1,\n" +
            "(select sum(preference_count) sum from dws_major_or_school_rank_preference) t2;")
    public List<MajorOrSchoolRankPreference> majorOrSchoolRankPreference();

    // 不同家庭水平使学生选择专业倾向
    @Select("select major_name, student_family_level, count(count) count\n" +
            "from dws_family_level_major_preference\n" +
            "group by major_name, student_family_level;")
    public List<FamilyLevelMajorPreference> familyLevelMajorPreference();


}
