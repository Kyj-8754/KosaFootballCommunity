package com.msa.kyj_prj.weather;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather-collect")
public class WeatherCollectorController {

    @Autowired
    private WeatherCollectorService weatherCollectorService;

    @GetMapping("/run")
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