package com.msa.kyj_prj.stadium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Stadium {
	private String SVCID;		// 서비스 번호
	private String SVCNM;		// 서비스명
	private String AREANM;		// 지역명
	private String PLACENM;		// 장소명
	private String SUBPLACENM;	// 상세주소명
	private String TELNO;		// 연락처
	private String ORGNM;		// 담당기관명
	private String SVCENDTELNO;	// 서비스담당자전화번호
	private String ADRES;		// 장소주소
	private String X;			// X 좌표
	private String Y;			// Y 좌표
	private String IMG_PATH;	// 이미지 주소
	private String V_MIN;		// 서비스이용시작기간
	private String V_MAX;		// 서비스이용종료기간
	private String NOTICE;		// 주의사항
	private String DTLCONT;		// 상세내용
	private String PRICE;
}
