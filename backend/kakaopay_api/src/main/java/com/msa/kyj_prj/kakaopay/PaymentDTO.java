package com.msa.kyj_prj.kakaopay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
	private Long id;
	private String tid;
	private String reservation_id;
	private String user_no;
	private int amount;
	private String status;
	private String paid_at;
	private String method;
	private String create_at;
}
