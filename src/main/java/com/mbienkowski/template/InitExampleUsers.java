package com.mbienkowski.template;


import static com.mbienkowski.template.user.UserRole.ADMIN;
import static com.mbienkowski.template.user.UserRole.CLIENT;

import com.mbienkowski.template.user.User;
import com.mbienkowski.template.user.UserRole;
import com.mbienkowski.template.user.UserService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitExampleUsers {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostConstruct
    void createUsers() {
        createUserWithRandomPassword("root", ADMIN);
        createUserWithRandomPassword("joe", CLIENT);
    }

    void createUserWithRandomPassword(String login, UserRole... roles) {
        var userExists = userService.isExisting(login);
        if (!userExists) {
            var password = RandomStringUtils.randomAlphabetic(16);
            var user = User.builder()
                .login(login)
                .passwordHash(passwordEncoder.encode(password))
                .roles(List.of(roles))
                .build();
            userService.save(user);
            log.info("User '{}' with roles '{}' created with password: {}", login, List.of(roles), password);
        }
    }
}
