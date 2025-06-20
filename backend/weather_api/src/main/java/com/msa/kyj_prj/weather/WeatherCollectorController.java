package com.msa.kyj_prj.weather;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather-collect")
public class WeatherCollectorController {

    @Autowired
    private WeatherCollectorService weatherCollectorService;

    @PostMapping("/run")
    public Map<String, Object> runCollection() {
        weatherCollectorService.collectAndStoreForecasts();
        return Map.of("result", "success");
    }
}
