package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.UserLevelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户等级配置Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface UserLevelConfigMapper extends BaseMapper<UserLevelConfig> {

    /**
     * 根据等级代码获取配置信息
     * 
     * @param levelCode 等级代码
     * @return 等级配置信息
     */
    UserLevelConfig selectByLevelCode(@Param("levelCode") String levelCode);

    /**
     * 根据用户收益获取对应等级配置
     * 
     * @param totalEarnings 总收益金额（分）
     * @return 等级配置信息
     */
    UserLevelConfig selectByEarnings(@Param("totalEarnings") Integer totalEarnings);
}
