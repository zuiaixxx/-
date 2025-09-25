package com.platform.agriculture.service.impl;

import com.platform.agriculture.domain.entity.Campus;
import com.platform.agriculture.mapper.CampusMapper;
import com.platform.agriculture.service.ICampusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 校区信息表（逻辑外键） 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@Service
public class CampusServiceImpl extends ServiceImpl<CampusMapper, Campus> implements ICampusService {

}
