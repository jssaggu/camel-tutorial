package com.jss.routes.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.jss.CamelApplication;
import com.jss.dto.WeatherDto;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.rest-dsl.enabled=true"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CamelSpringBootTest
public class RestDslTest {

    final String uriT = "http://localhost:%s/services/weather/%s";

    @LocalServerPort private int serverPort;

    @Autowired private TestRestTemplate testRestTemplate;

    @Test
    void givenValidCity_weatherUri_WillReturnCityData() {
        String london = "London";
        String uri = format(uriT, serverPort, london);
        WeatherDto weather = testRestTemplate.getForObject(uri, WeatherDto.class);
        assertNotNull(weather);
        assertEquals(london, weather.getCity());
    }

    @Test
    void givenInvalidCity_weatherUri_WillReturn404() {
        String london = "Foo";
        String uri = format(uriT, serverPort, london);
        ResponseEntity<Object> weather = testRestTemplate.getForEntity(uri, Object.class);
        assertNotNull(weather);
        assertEquals(404, weather.getStatusCode().value());
    }

    @Test
    void givenNewCity_weatherUri_WillCreateWeatherData() {
        String city = "Slough";
        String uri = format(uriT, serverPort, "");
        WeatherDto weather = WeatherDto.builder().city(city).temp("10").unit("C").build();
        ResponseEntity<WeatherDto> weatherResponse =
                testRestTemplate.postForEntity(uri, weather, WeatherDto.class);
        assertNotNull("Response can't be null", weatherResponse);
        assertEquals(200, weatherResponse.getStatusCode().value());
        assertEquals(city, weatherResponse.getBody().getCity());
        assertNotNull(weatherResponse.getBody().getId());
    }
}
