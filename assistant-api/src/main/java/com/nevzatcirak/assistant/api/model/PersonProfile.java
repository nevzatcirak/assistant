package com.nevzatcirak.assistant.api.model;

/**
 * Configuration data structure for the persona.
 */
public record PersonProfile(
    String firstName,
    String lastName,
    String role,
    String linkedinUrl,
    String cvPath
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
