package com.msa.kyj_prj.widget;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.msa.kyj_prj.weather.Weather;

@Mapper
public interface WidgetDAO {
	List<Weather> selectEarliest5TimesWeatherByRegionAndDate(
		    @Param("weather_region") String weather_region,
		    @Param("weather_fcst_date") String weather_fcst_date
		);
}
