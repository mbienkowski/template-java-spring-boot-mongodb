package com.mbienkowski.template.user.security;

import static com.mbienkowski.template.user.security.JwtTokenService.DEVICE_SECURITY_TOKEN_KEY;
import static java.util.List.of;
import static org.apache.commons.lang3.StringUtils.prependIfMissing;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mbienkowski.template.user.UserRole;
import com.mbienkowski.template.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        var jwtToken = request.getHeader(AUTHORIZATION);
        if (Strings.isBlank(jwtToken)) {
            log.debug("Authorization header not found or empty value");
            chain.doFilter(request, response);
            return;
        }

        // Decode the token if it is valid
        DecodedJWT decodedJwt;
        try {
            decodedJwt = jwtTokenService.decode(jwtToken);
        } catch (JWTVerificationException ex) {
            log.info("Failed to decode JWT");
            return;
        }

        // Checking if token expired
        if (jwtTokenService.isExpired(decodedJwt)) {
            log.info("JwtToken expired");
            return;
        }

        // Get user identity and set it on the spring security context
        var user = userService.find(decodedJwt.getSubject());
        if (!user.isActive()) {
            log.info("JwtToken is active, but user {} is not active.", user.getLogin());
            chain.doFilter(request, response);
            return;
        }

        // Checking is security token is held by one of the devices
        var userDevices = user.getDevices();
        var deviceSecurityToken = decodedJwt.getClaims().get(DEVICE_SECURITY_TOKEN_KEY).asString();
        if (userDevices.parallelStream().noneMatch(dev -> dev.getSecurityToken().equals(deviceSecurityToken))) {
            log.info("JwtToken does not match any of the device security tokens");
            return;
        }

        var userDetails = User.builder()
            .username(user.getLogin())
            .password(user.getPasswordHash())
            .authorities(user.getRoles().stream().map(this::buildGrantedAuthorityRoles).toList())
            .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            Optional.of(userDetails).map(UserDetails::getAuthorities).orElse(of()));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private GrantedAuthority buildGrantedAuthorityRoles(UserRole roleName) {
        var r = prependIfMissing(upperCase(roleName.name()), DEFAULT_ROLE_PREFIX);
        return new SimpleGrantedAuthority(r);
    }
}
