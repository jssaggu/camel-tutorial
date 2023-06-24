package com.jss.routes.rest;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;

// @Component
public class RouteTester extends RouteBuilder {

    static int httpSocketTimeoutMillis = 5_000;
    static int httpConnectionTimeoutMillis = 5_000;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .producerComponent("http")
                .component("servlet")
                .bindingMode(RestBindingMode.off);

        from("timer:insurance?period=10000")
                .log(LoggingLevel.INFO, "Calling Hello API now")
                .to(
                        "http://localhost:8080/hello?"
                                + "sleepTimeMills="
                                + (httpSocketTimeoutMillis * 2)
                                + "&httpClientConfigurer=myRestHttpClientConfigurer"
                                + "")
                .log(LoggingLevel.ERROR, "Response: ${body}");
    }

    @Bean
    public HttpClientConfigurer myRestHttpClientConfigurer() {

        SocketConfig socketConfig =
                SocketConfig.custom().setSoTimeout(httpSocketTimeoutMillis).build();

        RequestConfig requestConfig =
                RequestConfig.custom()
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
