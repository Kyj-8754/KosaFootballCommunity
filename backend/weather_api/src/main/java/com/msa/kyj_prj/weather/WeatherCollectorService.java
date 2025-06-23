package com.msa.kyj_prj.weather;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}