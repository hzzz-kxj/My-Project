package cn.edu.zut.collect_school.mapper;

import cn.edu.zut.collect_school.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SchoolMapper {
    /**
     * hive离线查询语句
     * @return
     */
    // 学校排名top10
    @Select("select * from dws_school_rank_top10")
    public List<SchoolRankTop10> schoolRankTop10();

    // 学生所在省份与学校所在省份比例
    @Select("select *  from dws_province_rate;")
    public List<ProvinceRate> provinceRate();

    // 学生学历分布
    @Select("select * from dws_student_education_level")
    public List<StudentEducationLevel> studentEducationLevel();

    // 所有专业学生平均收入top10
    @Select("select * from dws_major_income_top10")
    public List<MajorIncomeTop10> majorIncomeTop10();

    // 每个学校学生平均收入top10
    @Select("select * from dws_school_student_income_top10")
    public List<SchoolStudentIncomeTop10> schoolStudentIncomeTop10();

    // 学生选择什么专业的人数多top10
    @Select("select * from dws_major_number;")
    public List<MajorNumber> majorNumber();

    // 选择去哪上学数量top10
    @Select("select * from dws_province_number")
    public List<ProvinceNumber> provinceNumber();

    // 不同地区学校数量top10
    @Select("select * from dws_school_address_number")
    public List<SchoolAddressNumber> schoolAddressNumber();

    // 学生选择学校更倾向于专业排名还是学校排名
    @Select("select * from dws_major_or_school_rank_preference")
    public List<MajorOrSchoolRankPreference> majorOrSchoolRankPreference();

    // 不同家庭水平使学生选择专业倾向
    @Select("select * from dws_family_level_major_preference")
    public List<FamilyLevelMajorPreference> familyLevelMajorPreference();
}
