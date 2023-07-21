package com.mbienkowski.template.user.security.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Sha512Encoder implements PasswordEncoder {

    private static final String SHA_512 = "SHA-512";

    @Value("${security.password.hashing.salt}")
    private String hashingSalt;

    @Override
    @SneakyThrows
    public String encode(CharSequence raw) {
        var messageDigest = MessageDigest.getInstance(SHA_512);
        messageDigest.update(hashingSalt.getBytes(UTF_8));
        byte[] bytes = messageDigest.digest(raw.toString().getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    public boolean matches(CharSequence raw, String encoded) {
        return encode(raw).equals(encoded);
    }
}
