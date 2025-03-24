package com.example.parking_hub.mapper;

import com.example.parking_hub.model.AlertSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlertSettingMapper {
    // 특정 사용자의 알림 설정 조회
    List<AlertSetting> selectAlertSettingsByUserId(@Param("userId") int userId);

    // 특정 요일과 시간에 해당하는 알림 설정 조회
    List<AlertSetting> selectAlertSettingsByDayAndHour(
            @Param("dayOfWeek") String dayOfWeek,
            @Param("hour") int hour);

    // 알림 설정 등록
    int insertAlertSetting(AlertSetting alertSetting);

    // 알림 설정 수정
    int updateAlertSetting(AlertSetting alertSetting);

    // 알림 설정 삭제
    int deleteAlertSetting(@Param("id") int id);

    // 특정 사용자의 모든 알림 설정 삭제
    int deleteAllAlertSettingsByUserId(@Param("userId") int userId);
}
