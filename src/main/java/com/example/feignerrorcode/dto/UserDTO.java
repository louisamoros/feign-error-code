package com.example.feignerrorcode.dto;

public class UserDTO {

    public long id;
    public String username;

    public UserDTO() {
    }

    public UserDTO(long id, String username) {
        this.id = id;
        this.username = username;
    }
}
