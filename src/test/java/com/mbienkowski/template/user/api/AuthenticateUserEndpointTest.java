package com.mbienkowski.template.user.api;

import static com.mbienkowski.template.user.UserRole.CLIENT;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mbienkowski.template.BaseIntegrationTest;
import com.mbienkowski.template.user.api.dto.AuthenticateRequest;
import com.mbienkowski.template.user.security.JwtTokenService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthenticateUserEndpointTest extends BaseIntegrationTest {

    @Autowired
    JwtTokenService jwtTokenService;

    @Test
    @DisplayName("User should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        userService.create(login, password, List.of(CLIENT), emptyList());

        // When
        var device = getRandomDevice();
        var loginResponse = makeLoginRequest(login, password, device);

        // Then - a valid JWT token should be returned
        var decodedJwt = jwtTokenService.decode(loginResponse.getJwtToken());
        assertThat(decodedJwt.getSubject()).isEqualTo(login);

        // Then - a device should be added to authorized devices list
        var user = userService.find(login);
        assertThat(user.getDevices()).hasSize(1);
        assertThat(user.getDevices().get(0).getFingerprint()).isEqualTo(device.getFingerprint());
    }

    @Test
    @DisplayName("User should be able to login with multiple devices")
    void shouldBeAbleToLoginFromSecondDevice() throws Exception {
        // Given
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        userService.create(login, password, List.of(CLIENT), emptyList());
        var firstDevice = getRandomDevice();
        makeLoginRequest(login, password, firstDevice);

        // When
        var secondDevice = getRandomDevice();
        makeLoginRequest(login, password, secondDevice);

        // Then
        var user = userService.find(login);
        assertThat(user.getDevices()).hasSize(2);
    }

    @Test
    @DisplayName("User should not be able to login with inactive account")
    void shouldNotBeAbleToLoginWithInactiveAccount() throws Exception {
        // Given
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        var user = userService.create(login, password, List.of(CLIENT), emptyList());
        user.setActive(false);
        userService.save(user);

        // When
        var loginRequest = AuthenticateRequest.builder()
            .login(login)
            .password(password)
            .device(getRandomDevice())
            .build();

        var req = request(POST, getEndpointUrl(API_LOGIN_URL))
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest));

        // Then
        mvc.perform(req)
            .andExpect(status().isUnauthorized());
    }


}
