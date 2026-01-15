package com.microservice.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.common.dto.response.UserResponseDto;

import com.microservice.users.dto.request.UserUpdateDto;
import com.microservice.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User Management API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Create new user")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto response = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users with filtering")
    public ResponseEntity<List<UserResponseDto>> getActiveUsers() {
        List<UserResponseDto> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "Count users by status")
    public ResponseEntity<Long> countUsersByStatus(@PathVariable String status) {
        long count = userService.countUsersByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/emails/{status}")
    @Operation(summary = "Get user emails by status")
    public ResponseEntity<List<String>> getUserEmailsByStatus(@PathVariable String status) {
        List<String> emails = userService.getUserEmailsByStatus(status);
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/fullnames")
    @Operation(summary = "Get all users full names concatenated")
    public ResponseEntity<String> getUsersFullNames() {
        String names = userService.getUsersFullNames();
        return ResponseEntity.ok(names);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        UserResponseDto response = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update user")
    public ResponseEntity<UserResponseDto> partialUpdateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDto updateDto) {
        UserResponseDto response = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status")
    public ResponseEntity<List<UserResponseDto>> getUsersByStatus(@PathVariable String status) {
        List<UserResponseDto> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
}
