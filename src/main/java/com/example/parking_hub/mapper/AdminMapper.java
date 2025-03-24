package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    // 모든 관리자 조회
    List<Admin> selectAllAdmins();

    // 특정 관리자 조회 (ID 기준)
    Admin selectAdminById(@Param("adminId") int adminId);

    // 특정 관리자 조회 (이메일 기준)
    Admin selectAdminByEmail(@Param("adminEmail") String adminEmail);

    // 관리자 등록
    int insertAdmin(Admin admin);

    // 관리자 정보 수정
    int updateAdmin(Admin admin);

    // 관리자 삭제
    int deleteAdmin(@Param("adminId") int adminId);
}
