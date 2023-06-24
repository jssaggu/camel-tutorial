package com.jss.routes.kafka;

import static org.apache.camel.LoggingLevel.ERROR;

import java.util.Date;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * kafka-console-consumer --bootstrap-server localhost:9092 --topic stock-audit Read live from Kafka
 * docker cp stocks.csv docker_kafka_1:/bin/stocks.csv Copy To Kafka
 * kafka-console-producer--broker-listlocalhost:9092--topic stock-live Write to Kafka
 * kafka-console-producer--broker-listlocalhost:9092--topic stock-live < some-file.txt Upload a file
 */
@Component
@ConditionalOnProperty(name = "jss.camel.kafka.enabled", havingValue = "true")
public class StockKafkaRoute extends RouteBuilder {

    final String KAFKA_ENDPOINT = "kafka:%s?brokers=localhost:29092";

    @Override
    public void configure() throws Exception {
        fromF(KAFKA_ENDPOINT, "stock-live")
                .log(ERROR, "[${header.kafka.OFFSET}] [${body}]")
                .bean(StockPriceEnricher.class)
                .toF(KAFKA_ENDPOINT, "stock-audit");
    }

    private class StockPriceEnricher {
        public String enrichStockPrice(String stockPrice) {
            return stockPrice + "," + new Date();
        }
    }
}
