package com.msa.kyj_prj.weather;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeatherDAO {
    void insertWeather(Weather weather);
    List<Weather> selectWeatherByRegion(String region);
}
