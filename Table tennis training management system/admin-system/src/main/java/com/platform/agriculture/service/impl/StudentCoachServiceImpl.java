package com.platform.agriculture.service.impl;

import com.platform.agriculture.domain.entity.StudentCoach;
import com.platform.agriculture.mapper.StudentCoachMapper;
import com.platform.agriculture.service.IStudentCoachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学员选择教练关系表（使用 id 关联） 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Service
public class StudentCoachServiceImpl extends ServiceImpl<StudentCoachMapper, StudentCoach> implements IStudentCoachService {

}
