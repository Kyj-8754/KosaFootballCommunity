package com.msa.kyj_prj.weather;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/weather-collect")
@Tag(name = "날씨 예보 수집", description = "기상청 예보 데이터 관련 API를 통한 데이터 수집")
public class WeatherCollectorController {

    @Autowired
    private WeatherCollectorService weatherCollectorService;

    @GetMapping("/run")
    @Operation(summary = "단기예보 수집",description = "해당 메서드가 호출될 경우 서울의 단기 예보 데이터를 수집합니다.")
    public ResponseEntity<?> runCollection() {
        try {
            weatherCollectorService.collectAndStoreForecasts();
            return ResponseEntity.ok(Map.of("result", "success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "result", "fail",
                    "error", "예보 수집 중 오류 발생",
                    "message", e.getMessage()
                ));
        }
    }
}