package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 分页查询操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE 1=1 " +
            "AND (#{action} IS NULL OR #{action} = '' OR action = #{action}) " +
            "AND (#{operator} IS NULL OR #{operator} = '' OR operator_name LIKE CONCAT('%', #{operator}, '%')) " +
            "AND (#{module} IS NULL OR #{module} = '' OR module = #{module}) " +
            "AND (#{startTime} IS NULL OR created_at >= #{startTime}) " +
            "AND (#{endTime} IS NULL OR created_at <= #{endTime}) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<OperationLog> selectPageWithConditions(@Param("action") String action,
                                               @Param("operator") String operator,
                                               @Param("module") String module,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    /**
     * 统计操作日志总数
     */
    @Select("SELECT COUNT(*) FROM operation_logs WHERE 1=1 " +
            "AND (#{action} IS NULL OR #{action} = '' OR action = #{action}) " +
            "AND (#{operator} IS NULL OR #{operator} = '' OR operator_name LIKE CONCAT('%', #{operator}, '%')) " +
            "AND (#{module} IS NULL OR #{module} = '' OR module = #{module}) " +
            "AND (#{startTime} IS NULL OR created_at >= #{startTime}) " +
            "AND (#{endTime} IS NULL OR created_at <= #{endTime})")
    Long countWithConditions(@Param("action") String action,
                            @Param("operator") String operator,
                            @Param("module") String module,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计操作类型分布
     */
    @Select("SELECT action, COUNT(*) as count FROM operation_logs " +
            "WHERE created_at >= #{startTime} AND created_at <= #{endTime} " +
            "GROUP BY action ORDER BY count DESC")
    List<Map<String, Object>> selectActionStats(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计操作人分布
     */
    @Select("SELECT operator_name, COUNT(*) as count FROM operation_logs " +
            "WHERE created_at >= #{startTime} AND created_at <= #{endTime} " +
            "GROUP BY operator_name ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> selectOperatorStats(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 清理过期日志
     */
    @Select("DELETE FROM operation_logs WHERE created_at < #{expireTime}")
    int deleteExpiredLogs(@Param("expireTime") LocalDateTime expireTime);
}
