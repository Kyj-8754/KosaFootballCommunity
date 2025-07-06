package com.msa.kyj_prj.alarm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "JOIN_APPROVED"),
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "JOIN_REJECTED"),
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "CLUB_APPLY"),
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "CLUB_WITHDRAW"),   // 🔔 탈퇴 알림
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "CLUB_KICKED")      // 🔔 강퇴 알림
    // 예: 친구알람, 예약알람 등 필요한 타입 계속 추가
})

public interface AlarmMessage {
    String getType();
    String getSenderId();
    String getReceiverId();
    String getMessage();
    String getUrl();
}
