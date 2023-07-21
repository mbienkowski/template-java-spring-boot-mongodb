package com.mbienkowski.template;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbienkowski.template.user.User;
import com.mbienkowski.template.user.UserRole;
import com.mbienkowski.template.user.UserService;
import com.mbienkowski.template.user.api.dto.AuthenticateRequest;
import com.mbienkowski.template.user.api.dto.AuthenticateResponse;
import com.mbienkowski.template.user.device.Device;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    protected static final String API_LOGIN_URL = "/api/v1/user/authenticate";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ApplicationPortListener portListener;

    protected String getEndpointUrl(String path) {
        return "http://localhost:" + portListener.getServerPort() + path;
    }

    @SneakyThrows
    protected AuthorizedUser getRandomAuthorizedUser(UserRole... roles) {
        // Create User
        var login = randomAlphabetic(20) + "@template.com";
        var password = randomAlphabetic(10);
        var user = userService.create(login, password, List.of(roles), List.of());

        // Login User to get the JWT token
        var device = getRandomDevice();
        var loginResponse = makeLoginRequest(login, password, device);
        var jwtToken = loginResponse.getJwtToken();

        // Return TestUser with all information
        return AuthorizedUser.builder()
            .login(user.getLogin())
            .passwordHash(user.getPasswordHash())
            .roles(user.getRoles())
            .devices(user.getDevices())
            .jwtToken(jwtToken)
            .build();
    }

    protected Device getRandomDevice() {
        return Device.builder()
            .fingerprint(UUID.randomUUID().toString())
            .build();
    }

    protected AuthenticateResponse makeLoginRequest(String login, String password, Device device) throws Exception {
        var loginRequest = AuthenticateRequest.builder()
            .login(login)
            .password(password)
            .device(device)
            .build();

        var req = request(POST, getEndpointUrl(API_LOGIN_URL))
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest));

        var res = mvc.perform(req).andReturn().getResponse().getContentAsString();
        return mapper.readValue(res, AuthenticateResponse.class);
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class AuthorizedUser extends User {

        private String password;

        private String jwtToken;

    }

}
