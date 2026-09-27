package com.ianvink.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ianvink.application.entities.UserEntity;
import com.ianvink.application.entities.UserPermissionsEntity;
import com.ianvink.application.repositories.UserPermissionsRepository;
import com.ianvink.application.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPermissionsRepository userPermissionRepository;

    @Transactional
    public Optional<UserPermissionsEntity> getUserPermissions(Long userId) {
        return userPermissionRepository.findByUserIdWithUser(userId);
    }

    @Transactional
    public List<UserPermissionsEntity> getAllUserPermissions() {
        return userPermissionRepository.findAllWithUser();
    }

    @Transactional
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserPermissionsEntity updatePermissions(Long userId, boolean editSettings, boolean deleteRecords) {
        UserPermissionsEntity permissions = userPermissionRepository.findByUserIdWithUser(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Permissions profile not found for user ID: " + userId));
        return userPermissionRepository.save(permissions);
    }

    @Transactional
    public UserPermissionsEntity saveUserPermissions(UserPermissionsEntity permissions) {
        return userPermissionRepository.save(permissions);
    }

    @Transactional
    public UserEntity createUserWithDefaultPermissions(String firstName, String lastName) {
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        UserEntity savedUser = userRepository.save(user);

        UserPermissionsEntity permissions = new UserPermissionsEntity();
        permissions.setUser(savedUser);
        permissions.setCanViewFirstName(false);
        permissions.setCanViewLastName(false);
        permissions.setCanViewEmail(false);
        permissions.setCanViewPhoneNumber(false);
        permissions.setCanViewAddress(false);
        permissions.setCanViewBsn(false);

        userPermissionRepository.save(permissions);
        return savedUser;
    }

    @Transactional
    public void deleteUserAndPermissions(Long userId) {
        userPermissionRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }
}
