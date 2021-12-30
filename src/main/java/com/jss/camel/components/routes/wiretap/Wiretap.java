package com.jss.camel.components.routes.wiretap;

import com.jss.camel.dto.TransactionDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Date;

import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.RABBIT_URI;
import static org.apache.camel.LoggingLevel.ERROR;

@ConditionalOnProperty(name = "jss.camel.wiretap.enabled", havingValue = "true")
public class Wiretap extends RouteBuilder {

    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String AUDIT_TRANSACTION_ROUTE = "direct:audit-transaction";
    public static final String AUDIT = "audit-transactions";

    @Override
    public void configure() throws Exception {
        fromF(RABBIT_URI, SENDER, SENDER)
                .unmarshal().json(JsonLibrary.Jackson, TransactionDto.class)
                .wireTap(AUDIT_TRANSACTION_ROUTE)
                .process(this::enrichTransactionDto)
                .marshal().json(JsonLibrary.Jackson, TransactionDto.class)
                .toF(RABBIT_URI, RECEIVER, RECEIVER)
                .log(ERROR, "Money Transferred: ${body}")
        ;

        from(AUDIT_TRANSACTION_ROUTE)
                .process(this::enrichTransactionDto)
                .marshal().json(JsonLibrary.Jackson, TransactionDto.class)
                .toF(RABBIT_URI, AUDIT, AUDIT);
    }

    private void enrichTransactionDto(Exchange exchange) {
        TransactionDto dto = exchange.getMessage().getBody(TransactionDto.class);
        dto.setTransactionDate(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}