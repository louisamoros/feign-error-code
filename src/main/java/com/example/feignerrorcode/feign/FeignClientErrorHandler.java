package com.example.feignerrorcode.feign;

import com.example.feignerrorcode.dto.ApiErrorDTO;
import com.example.feignerrorcode.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class FeignClientErrorHandler implements ErrorDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientErrorHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Exception decode(final String methodKey,
                            final Response response) {
        try {
            String bodyString = convertInputStreamToString(response.body().asInputStream());
            ApiErrorDTO apiErrorDTO = MAPPER.readValue(bodyString, ApiErrorDTO.class);
            LOGGER.info("API response is error. Commerce API error code = {}", apiErrorDTO.getCode());
            return new ApiException(apiErrorDTO.getMessage());
        } catch (IOException e) {
            return new IllegalArgumentException("Could not extract error payload.", e);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, String.valueOf(StandardCharsets.UTF_8));
        return writer.toString();
    }

}
