package com.mbienkowski.template.user.security.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mbienkowski.template.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Sha512EncoderTest extends BaseIntegrationTest {

    @Autowired
    Sha512Encoder sha512Encoder;

    @Test
    @DisplayName("Should create valid SHA-512 hash")
    void shouldCreateSha512Hash() {
        // Given
        var plainText = "SHA-512 Example Plain Text";

        // When
        var hashedText = sha512Encoder.encode(plainText);

        // Then
        assertEquals(
            "6670cda5a9c8ed5acc70eb94e4163aa9d9a42cdb443e67d7ddda4a6eb419f5f97f65381f2cf93776b0b494c2f2319b87ef4515b7b87713510b3a16bca07162f2",
            hashedText
        );
    }

    @Test
    @DisplayName("Should match plain text with SHA-512 hash")
    void shouldMatchPlainTextWithSha512Hash() {
        // Given
        var plainText = "Lorem ipsum";
        var hashedText = sha512Encoder.encode(plainText);

        // When
        var isMatching = sha512Encoder.matches(plainText, hashedText);

        // Then
        assertTrue(isMatching);
    }

    @Test
    @DisplayName("Should not match plain text with SHA-512 hash from different plain text")
    void shouldNotMatchPlainTextWithSha512Hash() {
        // Given
        var plainText = "Lorem ipsum";
        var hashedText = sha512Encoder.encode(plainText);

        // When
        var isMatching = sha512Encoder.matches("Not matching", hashedText);

        // Then
        assertFalse(isMatching);
    }
}
