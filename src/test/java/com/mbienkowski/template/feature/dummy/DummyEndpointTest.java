package com.mbienkowski.template.feature.dummy;

import static com.mbienkowski.template.user.UserRole.ADMIN;
import static com.mbienkowski.template.user.UserRole.CLIENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mbienkowski.template.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DummyEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Not authorized Client should not be able to get any data")
    void shouldNotGetClientRestrictedData() throws Exception {
        // When
        var request = request(GET, getEndpointUrl("/api/v1/client/dummy"))
            .contentType(APPLICATION_JSON);

        // Then
        mvc.perform(request)
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Authorized Client should be able to get data User restricted data")
    void shouldGetClientRestrictedData() throws Exception {
        var user = getRandomAuthorizedUser(CLIENT);

        // When
        var request = request(GET, getEndpointUrl("/api/v1/client/dummy"))
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, user.getJwtToken());

        // Then
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().string("client-dummy-response"));
    }

    @Test
    @DisplayName("Authorized Client should not be able to get data Admin restricted data")
    void shouldNotGetAdminRestrictedData() throws Exception {
        var user = getRandomAuthorizedUser(CLIENT);

        // When
        var request = request(GET, getEndpointUrl("/api/v1/client/dummy"))
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, user.getJwtToken());

        // Then
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().string("client-dummy-response"));
    }

    @Test
    @DisplayName("Authorized Admin should be able to get data Admin restricted data")
    void shouldGetAdminRestrictedData() throws Exception {
        var user = getRandomAuthorizedUser(ADMIN);

        // When
        var request = request(GET, getEndpointUrl("/api/v1/admin/dummy"))
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, user.getJwtToken());

        // Then
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().string("admin-dummy-response"));
    }

}
