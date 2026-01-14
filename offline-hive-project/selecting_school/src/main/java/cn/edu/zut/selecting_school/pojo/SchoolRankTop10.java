package cn.edu.zut.collect_school.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SchoolRankTop10 {

    private String schoolName;

    private int schoolRank;

}
