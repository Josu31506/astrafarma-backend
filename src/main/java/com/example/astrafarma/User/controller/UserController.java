package com.example.astrafarma.User.controller;

import com.example.astrafarma.User.domain.UserCategoryStats;
import com.example.astrafarma.User.dto.UserDTO;
import com.example.astrafarma.User.domain.UserService;
import com.example.astrafarma.User.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/me")
    public UserDTO getAuthenticatedUserInfo() {
        return userService.getAuthenticatedUserInfo();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/me")
    public UserDTO updateUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.updateAuthenticatedUser(userRequestDto);
    }

    @GetMapping("/verify")
    public boolean verifyUser(@RequestParam String token) {
        return userService.verifyUser(token);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/me")
    public boolean deleteUser() {
        return userService.deleteAuthenticatedUser();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me/top-categories")
    public List<UserCategoryStats> getTop3Categories() {
        return userService.getTop3CategoriesForAuthenticatedUser();
    }
}