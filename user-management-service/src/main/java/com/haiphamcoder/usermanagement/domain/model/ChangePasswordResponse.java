package com.haiphamcoder.usermanagement.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("role")
    private String role;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("first_login")
    private Boolean firstLogin;

}
