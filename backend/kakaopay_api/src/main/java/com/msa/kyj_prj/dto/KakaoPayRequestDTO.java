package com.msa.kyj_prj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayRequestDTO {
	@Schema(description = "유저 PK", example = "SVC123")
	@JsonProperty("item_name")
    private String itemName;

	@Schema(description = "유저 PK", example = "SVC123")
    @JsonProperty("partner_order_id")
    private String partnerOrderId;

	@Schema(description = "유저 PK", example = "SVC123")
    @JsonProperty("partner_user_id")
    private String partnerUserId;

	@Schema(description = "유저 PK", example = "SVC123")
    @JsonProperty("total_amount")
    private int totalAmount;
    
	@Schema(description = "유저 PK", example = "SVC123")
    @JsonProperty("authCode")
    private String authCode;

}
