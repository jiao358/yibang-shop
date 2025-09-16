package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper
 *
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置组获取配置列表
     */
    @Select("SELECT * FROM system_configs WHERE config_group = #{configGroup} AND is_enabled = 1 ORDER BY sort_order ASC")
    List<SystemConfig> selectByConfigGroup(@Param("configGroup") String configGroup);

    /**
     * 根据配置键名获取配置
     */
    @Select("SELECT * FROM system_configs WHERE config_key = #{configKey} AND is_enabled = 1")
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);
}
