package com.jss.camel.components.routes.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static java.lang.Integer.valueOf;

@Configuration
@ConditionalOnProperty(name = {
        "jss.camel.rabbitmq.enabled"
},
        havingValue = "true")
public class RabbitmqConfiguration {

    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";

    @Bean
    public ConnectionFactory rabbitConnectionFactory2() {
        return factory();
    }

    public ConnectionFactory factory() {
        Properties properties = System.getProperties();
        String host = properties.getProperty(RMQ_HOST, "localhost");
        String port = properties.getProperty(RMQ_PORT, "5672");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(valueOf(port));
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }


    @Bean
    public HttpClientConfigurer ipsRestHttpClientConfigurer() {
        System.out.println("In ipsRestHttpClientConfigurer");

        int httpSocketTimeoutMillis = 20000;
        int httpConnectionTimeoutMillis = 20000;

        SocketConfig socketConfig = SocketConfig
                .custom()
                .setSoTimeout(httpSocketTimeoutMillis)
                .build();

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(httpConnectionTimeoutMillis)
                .setSocketTimeout(httpSocketTimeoutMillis)
                .build();

        return (HttpClientBuilder clientBuilder) -> {
            PoolingHttpClientConnectionManager conManager = new PoolingHttpClientConnectionManager();
            conManager.setMaxTotal(200);
            conManager.setDefaultMaxPerRoute(200);
            clientBuilder.setConnectionManager(conManager);
//            clientBuilder.setMaxConnPerRoute(200);
            clientBuilder.disableAutomaticRetries();
            clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

            clientBuilder.setDefaultRequestConfig(requestConfig);

            clientBuilder.useSystemProperties();

            clientBuilder.build();
        };
    }
}
