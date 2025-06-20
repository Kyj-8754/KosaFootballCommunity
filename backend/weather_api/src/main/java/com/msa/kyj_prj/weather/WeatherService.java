package com.msa.kyj_prj.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    @Autowired
    private WeatherDAO weatherDAO;

    public void saveWeatherList(List<Weather> weatherList) {
        for (Weather weather : weatherList) {
            weatherDAO.insertWeather(weather);
        }
    }

    public List<Weather> getWeatherByRegion(String region) {
        return weatherDAO.selectWeatherByRegion(region);
    }
}
