package com.msa.kyj_prj.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @PostMapping
    public Map<String, Object> saveWeatherData(@RequestBody List<Weather> weatherList) {
        weatherService.saveWeatherList(weatherList);
        return Map.of("result", "saved", "count", weatherList.size());
    }

    @GetMapping("/region/{region}")
    public List<Weather> getWeatherByRegion(@PathVariable String region) {
        return weatherService.getWeatherByRegion(region);
    }
}
