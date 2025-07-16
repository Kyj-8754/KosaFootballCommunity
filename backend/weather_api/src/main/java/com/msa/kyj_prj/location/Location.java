package com.msa.kyj_prj.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "위치 정보 DTO")
public class Location {
	
	@Schema(description = "지역명", example = "송파구")
    private String weather_location;   // 지역 (PK)
	
	@Schema(description = "x 좌표", example = "123")
    private String weather_location_x; // 지역 x좌표
	
	@Schema(description = "y 좌표", example = "123")
    private String weather_location_y; // 지역 y좌표
}
