package com.jss.routes.saga;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@Data
public class CreditService {

    final Map<String, Double> customerAccount = new HashMap<>();
    final Map<String, Double> orderAmount = new HashMap<>();

    public CreditService() {
        customerAccount.put("1000", 100D);
        customerAccount.put("2000", 200D);
        customerAccount.put("3000", 10.00);
    }

    public void makePayment(Exchange exchange) {
        OrderDto order = exchange.getMessage().getBody(OrderDto.class);
        String id = exchange.getIn().getHeader("id", String.class);
        double currentBalance = customerAccount.get(order.getCustomerId());
        System.out.println(
                "Taking Payment for Order ID: ["
                        + id
                        + "] "
                        + "Customer [Id:"
                        + order.getCustomerId()
                        + "] [Current Balance: "
                        + currentBalance
                        + "] [Deduct: "
                        + order.getAmount()
                        + "]");
        if (currentBalance < order.getAmount()) {
            throw new RuntimeException("Can't reserve. Insufficient Credit Balance.");
        }

        customerAccount.put(order.getCustomerId(), currentBalance - order.getAmount());
        orderAmount.put(id, order.getAmount());
        System.out.println(
                "Payment made for Order ID: ["
                        + id
                        + "] "
                        + "Customer [Id:"
                        + order.getCustomerId()
                        + "] [Current Balance: "
                        + customerAccount.get(order.getCustomerId())
                        + "]");
    }

    public void refundPayment(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        String customerId = exchange.getIn().getHeader("customerId", String.class);

        double currentBalance = customerAccount.get(customerId);

        System.out.println(
                "Returning Payment for Order ID: ["
                        + id
                        + "] "
                        + "Customer [Id:"
                        + customerId
                        + "] [Current Balance: "
                        + currentBalance
                        + "] [Refund: "
                        + orderAmount.get(id)
                        + "]");

        customerAccount.put(customerId, currentBalance + orderAmount.get(id));
        System.out.println(
                "Payment returned for Order ID: ["
                        + id
                        + "] "
                        + "Customer [Id:"
                        + customerId
                        + "] [Current Balance: "
                        + customerAccount.get(customerId)
                        + "]");
    }
}
