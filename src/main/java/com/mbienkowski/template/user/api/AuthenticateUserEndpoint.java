package com.mbienkowski.template.user.api;

import com.mbienkowski.template.exception.user.UserInactiveException;
import com.mbienkowski.template.user.UserService;
import com.mbienkowski.template.user.api.dto.AuthenticateRequest;
import com.mbienkowski.template.user.api.dto.AuthenticateResponse;
import com.mbienkowski.template.user.security.JwtTokenService;
import com.mbienkowski.template.user.security.JwtUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class AuthenticateUserEndpoint {

    public final AuthenticationManager authenticationManager;
    public final JwtTokenService jwtTokenService;
    public final JwtUserDetailsService jwtUserDetailsService;
    public final UserService userService;

    @PostMapping("/authenticate")
    public AuthenticateResponse authenticate(@RequestBody @Valid AuthenticateRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword());
        authenticationManager.authenticate(authToken);

        var userDetails = jwtUserDetailsService.loadUserByUsername(loginRequest.getLogin());
        var user = userService.find(userDetails.getUsername());

        if (!user.isActive()) {
            throw new UserInactiveException(user.getLogin());
        }

        var jwtToken = jwtTokenService.generateToken(user, loginRequest.getDevice());
        return AuthenticateResponse.builder().jwtToken(jwtToken).build();
    }

}
