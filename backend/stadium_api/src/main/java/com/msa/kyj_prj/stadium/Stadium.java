package com.msa.kyj_prj.stadium;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Builder
@NoArgsConstructor
public class Stadium {
	@Schema(description = "축구장서비스ID", example = "SVC123")
	private String SVCID;		// 서비스 번호
	@Schema(description = "서비스명", example = "SVC123")
	private String SVCNM;		// 서비스명
	@Schema(description = "지역명", example = "SVC123")
	private String AREANM;		// 지역명
	@Schema(description = "장소명", example = "SVC123")
	private String PLACENM;		// 장소명
	@Schema(description = "상세주소명", example = "SVC123")
	private String SUBPLACENM;	// 상세주소명
	@Schema(description = "연락처", example = "SVC123")
	private String TELNO;		// 연락처
	@Schema(description = "담당 기관명", example = "SVC123")
	private String ORGNM;		// 담당기관명
	@Schema(description = "서비스 담당자 전화번호", example = "SVC123")
	private String SVCENDTELNO;	// 서비스담당자전화번호
	@Schema(description = "경기장 ID", example = "SVC123")
	private String ADRES;		// 장소주소
	@Schema(description = "X 좌표", example = "SVC123")
	private String X;			// X 좌표
	@Schema(description = "Y 좌표", example = "SVC123")
	private String Y;			// Y 좌표
	@Schema(description = "이미지 주소", example = "SVC123")
	private String IMG_PATH;	// 이미지 주소
	@Schema(description = "서비스 이용 시작 시간", example = "SVC123")
	private String V_MIN;		// 서비스이용시작기간
	@Schema(description = "서비스 이용 종료 시간", example = "SVC123")
	private String V_MAX;		// 서비스이용종료기간
	@Schema(description = "주의 사항", example = "SVC123")
	private String NOTICE;		// 주의사항
	@Schema(description = "상세 내용", example = "SVC123")
	private String DTLCONT;		// 상세내용
}
