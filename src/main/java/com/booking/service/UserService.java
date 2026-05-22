package com.booking.service;

import com.booking.dto.user.UserCreateRequest;
import com.booking.dto.user.UserResponse;
import com.booking.dto.user.UserUpdateRequest;
import com.booking.entity.User;
import com.booking.entity.UserRole;
import com.booking.exception.DuplicateResourceException;
import com.booking.exception.ResourceNotFoundException;
import com.booking.mapper.UserMapper;
import com.booking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User with email {} already exists", request.getEmail());
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.GUEST);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        User updatedUser = userRepository.save(user);
        log.info("User with ID {} updated successfully", id);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
        log.info("User with ID {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching users with term: {}", searchTerm);
        return userRepository.searchByFirstNameOrLastNameOrEmail(searchTerm, pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
