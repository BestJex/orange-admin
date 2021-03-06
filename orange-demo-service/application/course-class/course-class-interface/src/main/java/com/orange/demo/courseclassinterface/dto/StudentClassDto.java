package com.orange.demo.courseclassinterface.dto;

import com.orange.demo.common.core.validator.UpdateGroup;
import com.orange.demo.common.core.validator.ConstDictRef;
import com.orange.demo.courseclassinterface.constant.ClassLevel;

import lombok.Data;

import javax.validation.constraints.*;

import java.util.Date;
import java.util.Map;

/**
 * StudentClassDto对象。
 *
 * @author Orange Team
 * @date 2020-08-08
 */
@Data
public class StudentClassDto {

    /**
     * 班级Id。
     */
    @NotNull(message = "数据验证失败，班级Id不能为空！", groups = {UpdateGroup.class})
    private Long classId;

    /**
     * 班级名称。
     */
    @NotBlank(message = "数据验证失败，班级名称不能为空！")
    private String className;

    /**
     * 学校Id。
     */
    @NotNull(message = "数据验证失败，所属校区不能为空！")
    private Long schoolId;

    /**
     * 学生班长Id。
     */
    @NotNull(message = "数据验证失败，班长不能为空！")
    private Long leaderId;

    /**
     * 已完成课时数量。
     */
    @NotNull(message = "数据验证失败，已完成课时不能为空！")
    private Integer finishClassHour;

    /**
     * 班级级别(0: 初级班 1: 培优班 2: 冲刺提分班 3: 竞赛班)。
     */
    @NotNull(message = "数据验证失败，班级级别不能为空！")
    @ConstDictRef(constDictClass = ClassLevel.class, message = "数据验证失败，班级级别为无效值！")
    private Integer classLevel;

    /**
     * 创建用户。
     */
    private Long createUserId;

    /**
     * 班级创建时间。
     */
    private Date createTime;

    /**
     * schoolId 字典关联数据。
     */
    private Map<String, Object> schoolIdDictMap;

    /**
     * leaderId 字典关联数据。
     */
    private Map<String, Object> leaderIdDictMap;

    /**
     * classLevel 常量字典关联数据。
     */
    private Map<String, Object> classLevelDictMap;
}
