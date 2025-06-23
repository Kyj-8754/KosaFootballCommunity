package com.msa.kyj_prj.weather;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {
    private Long weather_id;
    private String weather_region;
    private String weather_base_date;
    private String weather_base_time;
    private String weather_fcst_date;
    private String weather_fcst_time;
    private String weather_code;
    private String weather_value;
}
