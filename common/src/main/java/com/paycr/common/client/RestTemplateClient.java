package com.paycr.common.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateClient {

    public static RestTemplate getRestTemplate() {
        int connectionTimeout = 5;
        int readTimeout = 5;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout * 1000);
        requestFactory.setReadTimeout(readTimeout * 1000);
        return new RestTemplate(requestFactory);
    }

    public static RestTemplate getRestTemplate(int connectionTimeoutinSeconds,
                                               int readTimeoutinSeconds) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeoutinSeconds * 1000);
        requestFactory.setReadTimeout(readTimeoutinSeconds * 1000);
        return new RestTemplate(requestFactory);
    }

    public static RestTemplate getCustomRestTemplate(int connectionTimeOutInMillis,
                                               int readTimeOutInMillis) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(connectionTimeOutInMillis);
        requestFactory.setReadTimeout(readTimeOutInMillis);
        return new RestTemplate(requestFactory);
    }
}