package cn.edu.zut.collect_school.controller;

import cn.edu.zut.collect_school.mapper.SchoolMapper;
import cn.edu.zut.collect_school.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private SchoolMapper schoolMapper;

    // 学校排名top10
    @RequestMapping("/schoolRankTop10")
    public List<SchoolRankTop10> schoolRankTop10() {
        List<SchoolRankTop10> list = schoolMapper.schoolRankTop10();
        return list;
    }

    // 选择学校是否在生源地比例（饼状图）
    @RequestMapping("/provinceRate")
    public List<ProvinceRate> provinceRate() {
        List<ProvinceRate> list = schoolMapper.provinceRate();
        return list;
    }

    // 学生不同学历数量占比（大饼）
    @RequestMapping("/studentEducationLevel")
    public List<StudentEducationLevel> studentEducationLevel() {
        List<StudentEducationLevel> list = schoolMapper.studentEducationLevel();
        return list;
    }

    // 所有专业学生平均收入top10
    @RequestMapping("/majorIncomeTop10")
    public List<MajorIncomeTop10> majorIncomeTop10() {
        List<MajorIncomeTop10> list = schoolMapper.majorIncomeTop10();
        return list;
    }

    // 所有学校毕业学生平均收入top10
    @RequestMapping("/schoolStudentIncomeTop10")
    public List<SchoolStudentIncomeTop10> schoolStudentIncomeTop10() {
        List<SchoolStudentIncomeTop10> list = schoolMapper.schoolStudentIncomeTop10();
        return list;
    }

    // 学生选择什么专业的人数多top10
    @RequestMapping("/majorNumber")
    public List<MajorNumber> majorNumber() {
        List<MajorNumber> list = schoolMapper.majorNumber();
        return list;
    }

    // 学生选择什么地区的人数多top10
    @RequestMapping("/provinceNumber")
    public List<ProvinceNumber> provinceNumber() {
        List<ProvinceNumber> list = schoolMapper.provinceNumber();
        return list;
    }

    // 不同地区学校数量top10
    @RequestMapping("/schoolAddressNumber")
    public List<SchoolAddressNumber> schoolAddressNumber() {
        List<SchoolAddressNumber> list = schoolMapper.schoolAddressNumber();
        return list;
    }

    // 学生选择学校更倾向于专业排名还是学校排名（饼状图）
    @RequestMapping("/majorOrSchoolRankPreference")
    public List<MajorOrSchoolRankPreference> majorOrSchoolRankPreference() {
        List<MajorOrSchoolRankPreference> list = schoolMapper.majorOrSchoolRankPreference();
        return list;
    }

    // 不同地区学校数量top10
    @RequestMapping("/familyLevelMajorPreference")
    public List<FamilyLevelMajorPreference> familyLevelMajorPreference() {
        List<FamilyLevelMajorPreference> list = schoolMapper.familyLevelMajorPreference();
        return list;
    }
}
