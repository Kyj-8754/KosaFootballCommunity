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

        WeatherApiUtil weatherApiUtil = new WeatherApiUtil();
        
        for (Location loc : locations) {
            boolean success = false;

            while (!success) {
                try {
                    System.out.println("▶▶ [" + loc.getWeather_location() + "] 호출 시작");

                    List<Weather> forecasts = weatherApiUtil.fetchForecast(
                        loc.getWeather_location_x(),
                        loc.getWeather_location_y(),
                        loc.getWeather_location()
                    );

                    // 결과 유효성 검사
                    if (forecasts == null || forecasts.isEmpty()) {
                        throw new RuntimeException("예보 데이터가 없음");
                    }

                    for (Weather weather : forecasts) {
                        weatherDAO.insertWeather(weather);
                    }

                    success = true;
                    System.out.println("✅ [" + loc.getWeather_location() + "] 수집 성공");

                } catch (Exception e) {
                    System.err.println("❌ [" + loc.getWeather_location() + "] 수집 실패: " + e.getMessage());

                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        System.out.println("✅ 모든 지역 수집 완료");
    }


    // 매일 0시와 12시에 자동 실행
    @Scheduled(cron = "0 0 0,12 * * *", zone = "Asia/Seoul")
    public void scheduledForecastCollection() {
        collectAndStoreForecasts();
        System.out.println("스케줄링 실행 완료: " + java.time.LocalDateTime.now());
    }
}