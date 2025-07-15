package com.msa.kyj_prj.weather;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "날씨 예보 DTO")
public class Weather {
	
	@Schema(description = "예보 id", example = "123")
    private Long weather_id;
	
	@Schema(description = "지역명", example = "송파구")
    private String weather_region;
	
	@Schema(description = "예보를 한 날짜", example = "20250715")
    private String weather_base_date;
	
	@Schema(description = "예보를 한 시각", example = "1100")
    private String weather_base_time;
	
	@Schema(description = "예보 대상 날짜", example = "20250715")
    private String weather_fcst_date;
	
	@Schema(description = "예보 대상 시각", example = "1100")
    private String weather_fcst_time;
	
	@Schema(description = "예보 코드", example = "TMP")
    private String weather_code;
	
	@Schema(description = "예보 값", example = "24")
    private String weather_value;
}
