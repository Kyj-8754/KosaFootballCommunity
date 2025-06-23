package com.msa.kyj_prj.widget;

import com.msa.kyj_prj.weather.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/widget")
public class WidgetController {

    @Autowired
    private WidgetService widgetService;

    @GetMapping("/forecast")
    public List<Weather> getEarliest5Forecast(
            @RequestParam("region") String weather_region,
            @RequestParam("date") String weather_fcst_date
    ) {
        return widgetService.getEarliest5TimesForecast(weather_region, weather_fcst_date);
    }
}
