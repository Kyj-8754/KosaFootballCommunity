package com.msa.kyj_prj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class StadiumListDTO {
	@Schema(description = "축구장서비스ID", example = "S190522144836251426")
	private String SVCID;		// 서비스 번호
	@Schema(description = "서비스명", example = "풋살경기장A(구일역하부)")
	private String SVCNM;		// 서비스명
	@Schema(description = "지역명", example = "안양천 체육시설")
	private String AREANM;		// 지역명
	@Schema(description = "장소명", example = "안양천 안양천 체육시설")
	private String PLACENM;		// 장소명
	@Schema(description = "연락처", example = "02-860-2022")
	private String TELNO;		// 연락처
	@Schema(description = "경기장 주소", example = "서울특별시 구로구 구로동 642-71(안양천변 구일역 하부)")
	private String ADRES;		// 장소주소
	@Schema(description = "이미지 주소", example = "https://yeyak.seoul.go.kr/web/common/file/FileDown.do?file_id=SVC_IMG_FILE_241687")
	private String IMG_PATH;	// 이미지 주소
	@Schema(description = "서비스 이용 시작 시간", example = "08:00")
	private String V_MIN;		// 서비스이용시작기간
	@Schema(description = "서비스 이용 종료 시간", example = "22:00")
	private String V_MAX;		// 서비스이용종료기간
	@Schema(description = "가격", example = "10000")
	private String PRICE;
	@Schema(description = "평균 별점", example = "2.5")
	private Double avgRating;     // 추가
	@Schema(description = "리뷰 갯수", example = "20")
    private Integer reviewCount;  // 추가
	
}
