package com.haiphamcoder.cdp.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiphamcoder.cdp.domain.model.AuthenticationRequest;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

public class UsernamePasswordBodyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            try {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();
                AuthenticationRequest authenData = new ObjectMapper().readValue(requestBody, AuthenticationRequest.class);
                String username = authenData.getUsername() != null ? authenData.getUsername().trim() : "";
                String password = authenData.getPassword() != null ? authenData.getPassword() : "";

                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request, authRequest);

                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new InternalAuthenticationServiceException("Error while reading username and password from request body.", e);
            }
        }
    }

}

