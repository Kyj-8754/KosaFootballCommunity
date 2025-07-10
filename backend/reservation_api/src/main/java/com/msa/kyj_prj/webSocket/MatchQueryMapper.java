package com.msa.kyj_prj.webSocket;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.dto.WebsocketDTO;

import java.util.List;

@Mapper
public interface MatchQueryMapper {
    // 매치 참가자 user_no 리스트 조회
    List<WebsocketDTO> selectParticipantsUserNoByMatchId(int reservation_id);
}
