package com.msa.kyj_prj.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StadiumDTO {
	private String SVCID;		// 서비스 ID
	private String PLACENM;		// 구장 이름
	private String SVCNM;		// 구장 상세 이름
	private String ADRES;		// 구장 주소
	private String SVCENDTELNO;	// 구장 이용 전화번호
}
