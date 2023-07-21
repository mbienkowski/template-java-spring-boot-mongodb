package com.mbienkowski.template.user.device;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Based on https://www.npmjs.com/package/react-native-device-info
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class Device {

    private String fingerprint;

    private String systemName;

    private String systemVersion;

    /**
     * Security Token assigned to the device => Device Security Token.
     *
     * Thanks to this we are able to log out a single device instead of revoking all at once.
     */
    private String securityToken;

}
