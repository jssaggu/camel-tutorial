package com.jss.dto;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDto implements Serializable {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    // Boxed so Jackson 3 (default FAIL_ON_NULL_FOR_PRIMITIVES=true) tolerates request
    // payloads that omit the server-assigned id. @Builder.Default keeps the auto-id on the
    // builder path; deserialized payloads without an id are back-filled in WeatherDataProvider.
    @Builder.Default private Integer id = COUNTER.getAndIncrement();

    private String city;
    private String temp;
    private String unit;
    private String receivedTime;

    public static int nextId() {
        return COUNTER.getAndIncrement();
    }
}
