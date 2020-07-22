package com.example.feignerrorcode.feign;

import com.example.feignerrorcode.dto.ApiErrorDTO;
import com.example.feignerrorcode.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class FeignClientErrorHandler implements ErrorDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientErrorHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Exception decode(final String methodKey,
                            final Response response) {
        LOGGER.error("Feign client error handler. Method: {}, Response: {}", methodKey, response);
        if (Objects.isNull(response)) {
            return new IllegalArgumentException("API response is null.");
        }
        Response.Body body = response.body();
        if (Objects.isNull(body)) {
            return new IllegalArgumentException(
                    String.format("API body is null, could not handle it. Http status: %s", response.status())
            );
        }
        try {
            ApiErrorDTO apiErrorDTO = MAPPER.readValue(body.asReader(), ApiErrorDTO.class);
            LOGGER.info("API response is error. Commerce API error code = {}", apiErrorDTO.getCode());
            return new ApiException();
        } catch (IOException e) {
            return new IllegalArgumentException("Could not extract error payload.", e);
        }
    }


}
