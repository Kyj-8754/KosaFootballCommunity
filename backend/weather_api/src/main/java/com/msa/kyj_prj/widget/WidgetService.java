package com.msa.kyj_prj.widget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msa.kyj_prj.weather.Weather;

@Service
public class WidgetService {

    @Autowired
    private WidgetDAO widgetDAO;

    public List<Weather> getEarliest5TimesForecast(String weather_region, String weather_fcst_date) {
        return widgetDAO.selectEarliest5TimesWeatherByRegionAndDate(weather_region, weather_fcst_date);
    }
}
