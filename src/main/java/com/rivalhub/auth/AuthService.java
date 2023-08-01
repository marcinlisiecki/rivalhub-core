package com.rivalhub.auth;

import com.rivalhub.common.ErrorMessages;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()));

        } catch (Exception e) {
            throw new BadCredentialsException(ErrorMessages.BAD_CREDENTIALS);
        }

        UserData userData = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        String jwtToken = jwtService.generateToken(userData);
        return new LoginResponseDto(jwtToken);
    }
}
