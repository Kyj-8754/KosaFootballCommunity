package com.msa.kyj_prj.widget;

import com.msa.kyj_prj.weather.Weather;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/widget")
@Tag(name = "날씨 위젯", description = "수집한 데이터를 기반으로 단기 예보 데이터를 출력")
public class WidgetController {

    @Autowired
    private WidgetService widgetService;

    @GetMapping("/forecast")
    @Operation(summary = "단기예보 조회",description = "입력받은 지역과 시간 데이터를 기반으로 단기예보를 조회합니다.")
    public ResponseEntity<?> getEarliest5Forecast(
    		@Parameter(description = "서울 구 명", example = "송파구") @RequestParam("region") String weather_region,
    		@Parameter(description = "현재시각", example = "YYYYMMDD") @RequestParam("date") String weather_fcst_date
    ) {
        try {
            List<Weather> forecasts = widgetService.getEarliest5TimesForecast(weather_region, weather_fcst_date);
            return ResponseEntity.ok(forecasts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "날씨 위젯 예보 조회 실패",
                        "message", e.getMessage()
                    ));
        }
    }
}
