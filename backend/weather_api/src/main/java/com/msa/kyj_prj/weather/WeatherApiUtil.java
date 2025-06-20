package com.msa.kyj_prj.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherApiUtil {

    private static final String SERVICE_KEY = "여기에_인증키_입력";
    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    public static List<Weather> fetchForecast(String x, String y, String regionName) {
        // 날짜 및 기준시각 계산 (1일 8회 기준시각)
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String baseTime = getBaseTime(hour);
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String json = callApi(x, y, baseDate, baseTime);
        return parseWeatherJson(json, regionName);
    }

    private static String callApi(String x, String y, String baseDate, String baseTime) {
        try {
            OkHttpClient client = new OkHttpClient();
            String encodedKey = URLEncoder.encode(SERVICE_KEY, "UTF-8");

            String url = BASE_URL + "?" +
                    "serviceKey=" + encodedKey +
                    "&numOfRows=1000&pageNo=1" +
                    "&dataType=JSON" +
                    "&base_date=" + baseDate +
                    "&base_time=" + baseTime +
                    "&nx=" + x +
                    "&ny=" + y;

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Weather> parseWeatherJson(String json, String regionName) {
        List<Weather> weatherList = new ArrayList<>();
        if (json == null || json.isEmpty()) return weatherList;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(json)
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            for (JsonNode item : items) {
            	Weather w = new Weather();
            	w.setWeather_base_date(item.path("baseDate").asText());
            	w.setWeather_base_time(item.path("baseTime").asText());
            	w.setWeather_fcst_date(item.path("fcstDate").asText());
            	w.setWeather_fcst_time(item.path("fcstTime").asText());
            	w.setWeather_code(item.path("category").asText());
            	w.setWeather_value(item.path("fcstValue").asText());
            	w.setWeather_region(regionName);
            	weatherList.add(w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherList;
    }

    private static String getBaseTime(int hour) {
        // 기상청 기준 발표 시각 (정시 기준)
        int[] baseTimes = {2, 5, 8, 11, 14, 17, 20, 23};
        for (int i = baseTimes.length - 1; i >= 0; i--) {
            if (hour >= baseTimes[i]) {
                return String.format("%02d00", baseTimes[i]);
            }
        }
        return "2300"; // 자정 이전은 전날 23시
    }
}
