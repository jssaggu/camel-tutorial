package com.jss.camel.components.routes;

import com.jss.camel.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.jss.config.CamelConfiguration.RABBIT_URI;
import static org.apache.camel.LoggingLevel.ERROR;

@Component
@ConditionalOnProperty(name = "jss.camel.weather.enabled", havingValue = "true")
public class WeatherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        //Called by Rabbit on message in weather queue
        /*
        {
             "city": "London",
             "temp": "20",
             "unit": "C"
         }
        */
        fromF(RABBIT_URI, "weather", "weather")
                .log(ERROR, "Before Enrichment: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .log(ERROR, "After Enrichment: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, "weather-events", "weather-events")
                .to("file:///Users/jasvinder.saggu/projects/temp/camel-demos/?fileName=weather-events.txt&fileExist=Append")
        ;
    }

    private void enrichWeatherDto(Exchange exchange) {
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
