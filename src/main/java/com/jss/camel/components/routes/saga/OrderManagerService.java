package com.jss.camel.components.routes.saga;

import lombok.Data;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class OrderManagerService {

    final Map<String, OrderDto> orders = new HashMap<>();
    final Map<String, String> orderStatusMap = new HashMap<>();

    public void newOrder(Exchange exchange){
        OrderDto order =  exchange.getMessage().getBody(OrderDto.class);
        String id = exchange.getIn().getHeader("id", String.class);
        orders.put(id, order);
        System.out.println("Persisted Order. ID: [" + id + "] [" + order + "]");
    }

    public void cancelOrder(Exchange exchange){
        String id = exchange.getIn().getHeader("id", String.class);
        System.out.println("Cancelling Order. ID: [" + id + "]");
//        orders.remove(id);
    }

    public void shipOrder(Exchange exchange){
        String id = exchange.getIn().getHeader("id", String.class);
        OrderDto order = orders.get(id);
        System.out.println("Preparing to ship Order. ID: [" + order + "]");
        if(order.getQuantity() > 10){
            orderStatusMap.put(id, "Cancelled");
            throw new RuntimeException("Too many items to ship. Can't ship.");
        }
        orderStatusMap.put(id, "Shipped");
        System.out.println("Shipped Order. ID: [" + orders.get(id) + "]");
    }
}
