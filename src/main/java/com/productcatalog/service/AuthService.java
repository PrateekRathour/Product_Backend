package com.productcatalog.service;

import com.productcatalog.dto.auth.JwtResponse;
import com.productcatalog.dto.auth.LoginRequest;
import com.productcatalog.dto.auth.RegisterRequest;
import com.productcatalog.dto.auth.UserInfoResponse;

public interface AuthService {

    JwtResponse authenticateUser(LoginRequest loginRequest);

    UserInfoResponse registerUser(RegisterRequest registerRequest);

    UserInfoResponse getCurrentUserInfo(String username);
}
