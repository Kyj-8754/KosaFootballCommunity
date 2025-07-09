package com.msa.kyj_prj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayRequestDTO {
	@JsonProperty("item_name")
    private String itemName;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("total_amount")
    private int totalAmount;
    
    @JsonProperty("authCode")
    private String authCode;

}
