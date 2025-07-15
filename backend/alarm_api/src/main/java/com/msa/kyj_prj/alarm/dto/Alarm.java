package com.msa.kyj_prj.alarm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "알림(Alarm) DTO")
public class Alarm {

	@Schema(description = "알림 ID(PK)", example = "21")
	private int alarm_id;

	@Schema(description = "수신자 회원번호", example = "3")
	private int receiver_id;

	@Schema(description = "발신자 회원번호 (NULL 허용)", example = "7", nullable = true)
	private Integer sender_id; // NULL 허용이면 int → Integer로 바꾸는 것이 맞음

	@Schema(description = "알림 타입", example = "club_apply")
	private String type;

	@Schema(description = "알림 메시지 내용", example = "클럽 가입 신청이 도착했습니다.")
	private String message;

	@Schema(description = "관련 URL(알림 클릭 시 이동 이지만 미구현)", example = "/club/7")
	private String url;

	@Schema(description = "읽음 여부(Y:읽음, N:안읽음)", example = "N")
	private String read_yn;

	@Schema(description = "생성일시", example = "2024-07-16T14:32:10")
	private LocalDateTime created_at;

	
	@Schema(description = "수신자 이름", example = "김수신", nullable = true)
	private String receiver_name;

	@Schema(description = "발신자 이름", example = "이발신", nullable = true)
	private String sender_name;
}
