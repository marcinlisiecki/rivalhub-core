package com.rivalhub.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.common.dto.ErrorMessageDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("Access-Control-Allow-Origin", "*");

        ObjectMapper mapper = new ObjectMapper();
        ErrorMessageDto errorMessage = new ErrorMessageDto(ErrorMessages.NOT_AUTHENTICATED);
        response.getOutputStream().write(mapper.writeValueAsString(errorMessage).getBytes(StandardCharsets.UTF_8));
    }
}