package com.rivalhub.auth;

import com.rivalhub.common.ErrorMessages;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtTokenDto login(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()));

        } catch (AuthenticationException e) {
            throw new BadCredentialsException(ErrorMessages.BAD_CREDENTIALS);
        }

        UserData userData = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        String jwtToken = jwtService.generateToken(userData, generateExtraClaims(userData));
        String refreshToken = jwtService.generateRefresh(userData);

        return JwtTokenDto
                .builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtTokenDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserNotAuthenticatedException();
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = "";

        try {
            userEmail = jwtService.extractEmail(refreshToken);
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
            throw new UserNotAuthenticatedException();
        }

        if (userEmail == null) {
            throw new UserNotAuthenticatedException();
        }

        UserData userData = userRepository
                .findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        if (!jwtService.isTokenValid(refreshToken, userData)) {
            throw new UserNotAuthenticatedException();
        }

        String jwtToken = jwtService.generateToken(userData, generateExtraClaims(userData));

        return JwtTokenDto
                .builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Map<String, Object> generateExtraClaims(UserData userData) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", userData.getId());
        extraClaims.put("name", userData.getName());

        if (userData.getActivationTime() == null) {
            extraClaims.put("activationTime", null);
        } else {
            extraClaims.put("activationTime", userData.getActivationTime().toString());
        }

        return extraClaims;
    }
}
