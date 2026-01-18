package com.ReZherk.microservice_reservations.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ReZherk.microservice_reservations.dto.response.UserDto;
import com.ReZherk.microservice_reservations.exception.UserServiceException;

import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();

    }

    public Mono<UserDto> getUserById(Long userId) {
        return webClient.get()
                .uri("/api/users/register")
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorResume(e -> Mono.error(new UserServiceException("Failed to fetch user with id: " + userId, e)));
    }

    public Mono<Boolean> validateUser(Long userId) {
        return getUserById(userId)
                .map(user -> user != null && "ACTIVE".equals(user.getStatus()))
                .onErrorReturn(false);
    }

}
