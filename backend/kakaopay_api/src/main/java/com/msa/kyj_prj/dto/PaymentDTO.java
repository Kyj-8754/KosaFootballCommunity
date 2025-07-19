package com.msa.kyj_prj.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
	@Schema(description = "유저 PK", example = "SVC123")
	private Long id;
	@Schema(description = "유저 PK", example = "SVC123")
	private String tid;
	@Schema(description = "유저 PK", example = "SVC123")
	private String reservation_id;
	@Schema(description = "유저 PK", example = "SVC123")
	private String user_no;
	@Schema(description = "유저 PK", example = "SVC123")
	private int amount;
	@Schema(description = "유저 PK", example = "SVC123")
	private String status;
	@Schema(description = "유저 PK", example = "SVC123")
	private String paid_at;
	@Schema(description = "유저 PK", example = "SVC123")
	private String method;
	@Schema(description = "유저 PK", example = "SVC123")
	private String create_at;
}
