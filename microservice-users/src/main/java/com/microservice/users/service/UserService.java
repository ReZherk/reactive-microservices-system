package com.microservice.users.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ReZherk.common.dto.request.UserRequestDto;
import com.ReZherk.common.dto.response.UserResponseDto;

import com.microservice.users.dto.request.UserUpdateDto;
import com.microservice.users.entity.User;
import com.microservice.users.exception.UserAlreadyExistsException;
import com.microservice.users.exception.UserNotFoundException;
import com.microservice.users.mapper.UserMapper;
import com.microservice.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + requestDto.getEmail() + "already exist");
        }

        User entity = userMapper.toEntity(requestDto);
        User savedEntity = userRepository.save(entity);

        return userMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return userMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map((u) -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getActiveUsers() {
        return userRepository.findByStatus("ACTIVE").stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    public long countUsersByStatus(String status) {
        return userRepository.findByStatus(status).stream()
                .filter(user -> user.getStatus().equals(status))
                .count();
    }

    public List<String> getUserEmailsByStatus(String status) {
        return userRepository.findByStatus(status).stream()
                .map(User::getEmail)
                .filter(email -> email.endsWith(".com"))
                .collect(Collectors.toList());
    }

    public String getUsersFullNames() {
        return userRepository.findAll().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .reduce("", (acc, name) -> acc.isEmpty() ? name : acc + ", " + name);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(entity.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new UserAlreadyExistsException("Email " + updateDto.getEmail() + " is already in use");
            }
        }

        userMapper.updateEntityFromDto(updateDto, entity);
        User updatedEntity = userRepository.save(entity);
        return userMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto getUserByEmail(String email) {
        User entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        return userMapper.toDto(entity);
    }

    public List<UserResponseDto> getUsersByStatus(String status) {
        return userRepository.findByStatus(status).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

}
