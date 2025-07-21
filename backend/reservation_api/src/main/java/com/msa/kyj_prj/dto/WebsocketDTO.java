package com.msa.kyj_prj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebsocketDTO {
	private String matchTitle;
	private String senderId;
	private String receiverId;
	private Long match_id;
}
