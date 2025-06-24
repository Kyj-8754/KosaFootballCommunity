package com.msa.kyj_prj.weather;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.msa.kyj_prj.location.Location;
import com.msa.kyj_prj.location.LocationService;

@Service
public class WeatherCollectorService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private WeatherDAO weatherDAO;

    public void collectAndStoreForecasts() {
        List<Location> locations = locationService.getAllLocations();
        weatherDAO.truncateWeather();

        for (Location loc : locations) {
            // API 호출
            List<Weather> forecasts = WeatherApiUtil.fetchForecast(
                loc.getWeather_location_x(),
                loc.getWeather_location_y(),
                loc.getWeather_location()
            );

            // DB 저장
            for (Weather weather : forecasts) {
                weatherDAO.insertWeather(weather);
            }
        }
    }

    // 매일 0시와 12시에 자동 실행
    @Scheduled(cron = "0 0 0,12 * * *")
    public void scheduledForecastCollection() {
        collectAndStoreForecasts();
        System.out.println("스케줄링 실행 완료: " + java.time.LocalDateTime.now());
    }
}