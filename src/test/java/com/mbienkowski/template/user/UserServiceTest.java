package com.mbienkowski.template.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.mbienkowski.template.BaseIntegrationTest;
import com.mbienkowski.template.user.security.crypto.Sha512Encoder;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest extends BaseIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    Sha512Encoder sha512Encoder;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should save and find the user in the database")
    void shouldSaveAndFindUser() {
        // Given
        var login = "john@example.com";
        var password = RandomStringUtils.randomAlphanumeric(16);

        var user = User.builder()
            .login(login)
            .passwordHash(passwordEncoder.encode(password))
            .build();
        userService.save(user);

        // When
        var userFound = userService.find(login);

        // Then
        assertThat(userFound.getLogin()).isEqualTo(login);
        assertThat(userService.getUserId(login)).isEqualTo(userFound.getId());
        assertThat(passwordEncoder.matches(password, userFound.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("Should update the user's data in the database")
    void shouldUpdateUsersData() {
        // Given
        var login = "bobby@example.com";
        var user = userService.save(User.builder()
            .login(login)
            .passwordHash("first-value")
            .build()
        );

        assertThat(user.getVersion()).isZero();
        assertThat(user.getPasswordHash()).isEqualTo("first-value");

        // When
        user.setPasswordHash("second-value");
        var updatedUser = userService.save(user);

        // Then
        assertThat(updatedUser.getVersion()).isEqualTo(1);
        assertThat(updatedUser.getPasswordHash()).isEqualTo("second-value");
    }
}
