package com.riya.dentalbot.auth.service;

import com.riya.dentalbot.auth.dto.LoginRequest;
import com.riya.dentalbot.auth.dto.LoginResponse;
import com.riya.dentalbot.auth.dto.RegisterRequest;
import com.riya.dentalbot.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

}