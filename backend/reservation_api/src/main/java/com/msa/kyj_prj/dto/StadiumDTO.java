package com.msa.kyj_prj.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StadiumDTO {
	@Schema(description = "축구장서비스ID", example = "SVC123")
	private String SVCID;		// 서비스 ID
	@Schema(description = "장소명", example = "SVC123")
	private String PLACENM;		// 구장 이름
	@Schema(description = "서비스명", example = "SVC123")
	private String SVCNM;		// 구장 상세 이름
	@Schema(description = "경기장 주소", example = "SVC123")
	private String ADRES;		// 구장 주소
	@Schema(description = "서비스 담당자 전화번호", example = "SVC123")
	private String SVCENDTELNO;	// 구장 이용 전화번호
}
