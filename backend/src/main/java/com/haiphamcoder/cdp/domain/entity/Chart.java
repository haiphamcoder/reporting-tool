package com.haiphamcoder.cdp.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.cdp.shared.BaseEntity;
import com.haiphamcoder.cdp.shared.converter.JsonNodeStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "chart")
public class Chart extends BaseEntity{
    
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonProperty("user")
    private User user;

    @Column(name = "description", nullable = true)
    @JsonProperty("description")
    private String description;

    @Column(name = "config", nullable = false)
    @Convert(converter = JsonNodeStringConverter.class)
    private JsonNode config;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @JsonProperty("is_deleted")
    private Boolean isDeleted = false;
    
}
