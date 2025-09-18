package com.yibang.taskmall.hsf.impl;

import com.yibang.taskmall.hsf.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具服务实现类
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
public class TimeServiceImpl implements TimeService {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public String getCurrentTime() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    @Override
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public long getCurrentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public String getCurrentTime(String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.now().format(formatter);
        } catch (Exception e) {
            log.error("时间格式化失败，使用默认格式: {}", e.getMessage());
            return getCurrentTime();
        }
    }

    @Override
    public Map<String, Object> getTimeInfo() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> timeInfo = new HashMap<>();
        
        timeInfo.put("currentTime", now.format(DEFAULT_FORMATTER));
        timeInfo.put("currentDate", now.format(DATE_FORMATTER));
        timeInfo.put("currentTimeOnly", now.format(TIME_FORMATTER));
        timeInfo.put("timestamp", getCurrentTimestamp());
        timeInfo.put("timestampSeconds", getCurrentTimestampSeconds());
        timeInfo.put("year", now.getYear());
        timeInfo.put("month", now.getMonthValue());
        timeInfo.put("day", now.getDayOfMonth());
        timeInfo.put("dayOfWeek", now.getDayOfWeek().getValue());
        timeInfo.put("hour", now.getHour());
        timeInfo.put("minute", now.getMinute());
        timeInfo.put("second", now.getSecond());
        
        return timeInfo;
    }

    @Override
    public String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    @Override
    public String getCurrentTimeOnly() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    @Override
    public int getCurrentDayOfWeek() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }

    @Override
    public int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }

    @Override
    public int getCurrentMonth() {
        return LocalDateTime.now().getMonthValue();
    }

    @Override
    public int getCurrentDayOfMonth() {
        return LocalDateTime.now().getDayOfMonth();
    }
}
