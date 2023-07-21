package com.mbienkowski.template.user.device;

import static com.mbienkowski.template.user.UserRole.CLIENT;
import static com.mbienkowski.template.user.security.JwtTokenService.DEVICE_SECURITY_TOKEN_KEY;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mbienkowski.template.BaseIntegrationTest;
import com.mbienkowski.template.user.security.JwtTokenService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeviceTest extends BaseIntegrationTest {

    @Autowired
    JwtTokenService jwtTokenService;

    @Test
    @DisplayName("User should not be able to make request with removed device")
    void shouldNotBeAbleToMakeRequestWithRemovedDevice() throws Exception {
        // Given
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        userService.create(login, password, List.of(CLIENT), emptyList());
        var removedDe = getRandomDevice();
        var loginResponse = makeLoginRequest(login, password, removedDe);

        // Removing the device
        var user = userService.find(login);
        user.getDevices().clear();
        userService.save(user);

        // When
        var request = request(GET, getEndpointUrl("/api/info"))
            .contentType(APPLICATION_JSON)
            .content(loginResponse.getJwtToken());

        // Then
        mvc.perform(request)
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("User should receive JWT with the same device security token in JWT when logging in with the same device")
    void shouldReceiveSameSecurityTokenWhenLoggingInWithSameDevice() throws Exception {
        // Given
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        userService.create(login, password, List.of(CLIENT), emptyList());
        var device = getRandomDevice();
        var loginResponse1 = makeLoginRequest(login, password, device);

        // When
        var loginResponse2 = makeLoginRequest(login, password, device);

        // Then
        var jwt1 = jwtTokenService.decode(loginResponse1.getJwtToken());
        var jwt2 = jwtTokenService.decode(loginResponse2.getJwtToken());

        var deviceSecurityToken1 = jwt1.getClaims().get(DEVICE_SECURITY_TOKEN_KEY);
        var deviceSecurityToken2 = jwt2.getClaims().get(DEVICE_SECURITY_TOKEN_KEY);

        assertThat(deviceSecurityToken1).hasToString(deviceSecurityToken2.toString());
    }

}
