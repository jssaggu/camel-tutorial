package com.jss.camel.components.rest;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//@Component
public class RouteTester extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .producerComponent("http")
                .component("servlet").bindingMode(RestBindingMode.off);

        from("timer:insurance?period=10")
                .to("http://localhost:8080/api/hello?httpClientConfigurer=myRestHttpClientConfigurer")
                .log(LoggingLevel.ERROR, "Response: ${body}")
        ;
    }

    @Bean
    public HttpClientConfigurer myRestHttpClientConfigurer() {

        int httpSocketTimeoutMillis = 5_000;
        int httpConnectionTimeoutMillis = 5_000;
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
            clientBuilder.disableAutomaticRetries();
            clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

            clientBuilder.setDefaultRequestConfig(requestConfig);

            clientBuilder.useSystemProperties();

            clientBuilder.build();
        };
    }
}
