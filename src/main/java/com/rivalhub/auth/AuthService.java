package com.rivalhub.auth;

import com.rivalhub.common.ErrorMessages;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
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

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", userData.getId());
        extraClaims.put("name", userData.getName());

        if (userData.getActivationTime() == null) {
            extraClaims.put("activationTime", null);
        } else {
            extraClaims.put("activationTime", userData.getActivationTime().toString());
        }

        String jwtToken = jwtService.generateToken(userData, extraClaims);
        return new JwtTokenDto(jwtToken);
    }
}
