package com.haiphamcoder.cdp.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.cdp.domain.model.SourceShareComposeKey;
import com.haiphamcoder.cdp.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Entity
@Table(name = "source_share")
@IdClass(SourceShareComposeKey.class)
public class SourceShare extends BaseEntity {

    @Id
    private Source source;

    @Id
    private User user;

    @Column(name = "permission")
    @JsonProperty("permission")
    private String permission;

}
