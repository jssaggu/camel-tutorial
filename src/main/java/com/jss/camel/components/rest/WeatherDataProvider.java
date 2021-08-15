package com.jss.camel.components.rest;

import com.jss.camel.dto.WeatherDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "jss.camel.weather.enabled", havingValue = "true")
public class WeatherDataProvider {

    private static Map<String, WeatherDto> weatherData = new HashMap<>();

    public WeatherDataProvider() {
        WeatherDto dto = WeatherDto.builder().city("London").temp("10").unit("C").receivedTime(new Date().toString()).id(1).build();
        weatherData.put("LONDON", dto);
    }

    public WeatherDto getCurrentWeather(String city) {
        return weatherData.get(city.toUpperCase());
    }

    public void setCurrentWeather(WeatherDto dto) {
        dto.setReceivedTime(new Date().toString());
        weatherData.put(dto.getCity().toUpperCase(), dto);
    }
}