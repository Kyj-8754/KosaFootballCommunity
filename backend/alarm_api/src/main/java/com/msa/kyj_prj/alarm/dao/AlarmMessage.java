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
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "CLUB_WITHDRAW"),  
    @JsonSubTypes.Type(value = AlarmMessageDTO.class, name = "CLUB_KICKED"),
    @JsonSubTypes.Type(value = MatchAlarmMessageDTO.class, name = "MATCH_APPLY"),
    @JsonSubTypes.Type(value = MatchAlarmMessageDTO.class, name = "MATCH_APPROVED"),   // ✅ 추가!
    @JsonSubTypes.Type(value = MatchAlarmMessageDTO.class, name = "MATCH_REJECTED")    // ✅ 추가!
    // 이 밑으로 계속 기능 추가 
})


public interface AlarmMessage {
    String getType();
    String getSenderId();
    String getReceiverId();
    String getMessage();
    String getUrl();
}
