package com.yourpackage.security;

import com.yourpackage.exception.ResourceNotFoundException;
import com.yourpackage.user.User;
import com.yourpackage.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new CurrentUser(
                user.getId(),
                user.getClinic().getId(),
                user.getEmail(),
                user.getRole().name());
    }

}