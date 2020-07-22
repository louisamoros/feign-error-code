package com.example.feignerrorcode.feign;

import com.example.feignerrorcode.dto.UserDTO;

public class CustomerService extends RestService<CustomerClient> {

    public CustomerService(String url, String username, String password) {
        super(url, username, password);
    }

    public UserDTO get(String brand, String sessionToken) {
        return client.get(brand, sessionToken);
    }

}
