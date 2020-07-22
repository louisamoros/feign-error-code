package com.example.feignerrorcode.feign;

import com.example.feignerrorcode.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RequestMapping("/{" + CustomerClient.BRAND + "}")
public interface CustomerClient {

    String BRAND = "brand";
    String SESSION_TOKEN = "sessionToken";

    @GetMapping(consumes = APPLICATION_JSON_VALUE, path = "/customer")
    UserDTO get(
            @PathVariable(BRAND) String brand,
            @RequestHeader(SESSION_TOKEN) String sessionToken
    );

}
