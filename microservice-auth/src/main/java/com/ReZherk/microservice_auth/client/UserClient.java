package com.ReZherk.microservice_auth.client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.common.dto.response.UserResponseDto;

@FeignClient(name = "msvc-users", url = "localhost:9090/api/users")
public interface UserClient {

    @PostMapping("/register")
    UserResponseDto registerUser(@RequestBody UserRequestDto requestDto);

}
