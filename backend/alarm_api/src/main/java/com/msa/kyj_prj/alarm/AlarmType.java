package com.msa.kyj_prj.alarm;

public enum AlarmType {
    JOIN_REQUEST,     // 클럽 가입 신청
    JOIN_APPROVED,    // 가입 승인
    JOIN_REJECTED,    // 가입 거절
    MATCH_INVITE,     // 경기 초대 (예: 추후 확장용)
    GENERAL           // 일반 알림
}
