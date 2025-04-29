package com.haiphamcoder.cdp.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiphamcoder.cdp.domain.model.ConnectorCategory;
import com.haiphamcoder.cdp.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(name = "connector")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Connector extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Builder.Default
    private Long id = null;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConnectorCategory category;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

}
