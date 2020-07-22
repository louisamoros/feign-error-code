package com.example.feignerrorcode.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.GenericTypeResolver;

public class RestService<T> {

    protected final T client;
    private final Class<T> targetClass;
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public RestService(String url, String username, String password) {
        this.targetClass = getTargetClass();
        this.client = buildRestClientBuilder(url, username, password);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getTargetClass() {
        return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), RestService.class);
    }

    private T buildRestClientBuilder(String url, String username, String password) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new JacksonEncoder(MAPPER))
                .decoder(new JacksonDecoder(MAPPER))
                .errorDecoder(new FeignClientErrorHandler())
                .logLevel(feign.Logger.Level.FULL)
                .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                .target(targetClass, url);
    }
}
