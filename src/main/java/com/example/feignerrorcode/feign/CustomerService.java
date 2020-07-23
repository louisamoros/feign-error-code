package com.example.feignerrorcode.feign;

import com.example.feignerrorcode.dto.UserDTO;
import com.example.feignerrorcode.exception.ApiException;

public class CustomerService extends RestService<CustomerClient> {

    public CustomerService(String url, String username, String password) {
        super(url, username, password);
    }

    public UserDTO get(String brand, String sessionToken) throws ApiException {
        return client.get(brand, sessionToken);
    }

}
