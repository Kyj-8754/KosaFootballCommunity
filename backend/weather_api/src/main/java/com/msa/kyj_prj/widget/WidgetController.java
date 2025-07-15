package com.msa.kyj_prj.widget;

import com.msa.kyj_prj.weather.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/widget")
public class WidgetController {

    @Autowired
    private WidgetService widgetService;

    @GetMapping("/forecast")
    public ResponseEntity<?> getEarliest5Forecast(
            @RequestParam("region") String weather_region,
            @RequestParam("date") String weather_fcst_date
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
