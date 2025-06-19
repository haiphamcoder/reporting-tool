package com.haiphamcoder.authentication.domain.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("first_login")
    private Boolean firstLogin;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("password")
    private String password;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("role")
    private String role;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;
    
}
