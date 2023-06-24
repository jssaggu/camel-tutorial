package com.jss.routes.saga;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {
    private String orderId;
    private String customerId;
    private String itemName;
    private int quantity;
    private double amount;
}
