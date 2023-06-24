package com.jss.routes.wiretap;

import static org.apache.camel.LoggingLevel.ERROR;

import com.jss.dto.TransactionDto;
import com.jss.routes.rabbitmq.RabbitmqConfiguration;
import java.util.Date;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(name = "jss.camel.wiretap.enabled", havingValue = "true")
public class Wiretap extends RouteBuilder {

    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String AUDIT_TRANSACTION_ROUTE = "direct:audit-transaction";
    public static final String AUDIT = "audit-transactions";

    @Override
    public void configure() throws Exception {
        fromF(RabbitmqConfiguration.RABBIT_URI, SENDER, SENDER)
                .unmarshal()
                .json(JsonLibrary.Jackson, TransactionDto.class)
                .wireTap(AUDIT_TRANSACTION_ROUTE)
                .process(this::enrichTransactionDto)
                .marshal()
                .json(JsonLibrary.Jackson, TransactionDto.class)
                .toF(RabbitmqConfiguration.RABBIT_URI, RECEIVER, RECEIVER)
                .log(ERROR, "Money Transferred: ${body}");

        from(AUDIT_TRANSACTION_ROUTE)
                .process(this::enrichTransactionDto)
                .marshal()
                .json(JsonLibrary.Jackson, TransactionDto.class)
                .toF(RabbitmqConfiguration.RABBIT_URI, AUDIT, AUDIT);
    }

    private void enrichTransactionDto(Exchange exchange) {
        TransactionDto dto = exchange.getMessage().getBody(TransactionDto.class);
        dto.setTransactionDate(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
