package com.example.parking_hub.mapper;

import com.example.parking_hub.model.UserHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HistoryMapper {
    // 특정 사용자의 방문 기록 조회
    List<UserHistory> selectHistoriesByUserId(@Param("userId") int userId);

    // 특정 사용자의 최근 방문 기록 조회 (제한 개수)
    List<UserHistory> selectRecentHistoriesByUserId(
            @Param("userId") int userId,
            @Param("limit") int limit);

    // 방문 기록 등록
    int insertHistory(UserHistory history);

    // 방문 기록 삭제
    int deleteHistory(@Param("id") int id);

    // 특정 사용자의 모든 방문 기록 삭제
    int deleteAllHistoriesByUserId(@Param("userId") int userId);
}
