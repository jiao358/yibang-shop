package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统配置Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键前缀查询配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_key LIKE CONCAT(#{prefix}, '%') AND is_public = 1 ORDER BY id ASC")
    List<SystemConfig> selectByKeyPrefix(@Param("prefix") String prefix);

    /**
     * 根据配置键查询配置
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{key} AND is_public = 1 LIMIT 1")
    SystemConfig selectByKey(@Param("key") String key);

    /**
     * 批量更新配置状态
     */
    @Select("UPDATE system_config SET is_public = #{isPublic}, updated_at = NOW() WHERE id IN (#{ids})")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isPublic") Boolean isPublic);
}