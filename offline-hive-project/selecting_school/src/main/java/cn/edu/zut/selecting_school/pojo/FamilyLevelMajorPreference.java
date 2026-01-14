package cn.edu.zut.collect_school.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class FamilyLevelMajorPreference {

    private String majorName;

    private String studentFamilyLevel;

    private int count;
}
