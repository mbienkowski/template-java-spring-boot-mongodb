package com.mbienkowski.template.user.api.dto;

import com.mbienkowski.template.user.device.Device;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequest {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotNull
    private Device device;

}
