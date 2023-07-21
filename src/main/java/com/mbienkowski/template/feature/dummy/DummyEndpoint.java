package com.mbienkowski.template.feature.dummy;


import static com.mbienkowski.template.user.UserRole.RoleNames.ADMIN;
import static com.mbienkowski.template.user.UserRole.RoleNames.CLIENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class DummyEndpoint {

    @RolesAllowed(CLIENT)
    @GetMapping("/api/v1/client/dummy")
    public String getUserData() {
        return "client-dummy-response";
    }

    @RolesAllowed(ADMIN)
    @GetMapping("/api/v1/admin/dummy")
    public String getAdminData() {
        return "admin-dummy-response";
    }

}
