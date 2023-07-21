package com.mbienkowski.template.user.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mbienkowski.template.user.User;
import com.mbienkowski.template.user.UserService;
import com.mbienkowski.template.user.device.Device;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    public static final String DEVICE_SECURITY_TOKEN_KEY = "securityToken";

    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofDays(365 * 5);

    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final UserService userService;

    public JwtTokenService(@Value("${security.jwt.encryption.key}") final String secret, UserService userService) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
        this.userService = userService;
    }

    public String generateToken(User user, Device device) {
        var securityToken = UUID.randomUUID().toString();
        var existingDevice = user.getDevices().parallelStream().filter(dev -> dev.getFingerprint().equals(device.getFingerprint())).findFirst();

        if (existingDevice.isPresent()) {
            securityToken = existingDevice.get().getSecurityToken();
        } else {
            device.setSecurityToken(securityToken);
            user.getDevices().add(device);
            userService.save(user);
        }

        return JWT.create()
            .withSubject(user.getLogin())
            .withIssuer(JwtTokenService.class.getSimpleName())
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
            .withPayload(Map.of(DEVICE_SECURITY_TOKEN_KEY, securityToken))
            .sign(this.hmac512);
    }

    public DecodedJWT decode(String token) {
        return verifier.verify(token);
    }

    public boolean isExpired(DecodedJWT token) {
        return Instant.now().isAfter(token.getExpiresAtAsInstant());
    }

}
