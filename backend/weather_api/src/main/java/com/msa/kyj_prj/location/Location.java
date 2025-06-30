package com.msa.kyj_prj.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private String weather_location;   // 지역 (PK)
    private String weather_location_x; // 지역 x좌표
    private String weather_location_y; // 지역 y좌표
}
