package com.haiphamcoder.cdp.domain.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.cdp.shared.BaseEntity;
import com.haiphamcoder.cdp.shared.converter.MapStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "source_connector")

public class SourceConnector extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "description", nullable = true)
    @JsonProperty("description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    @JsonProperty("source")
    private Source source;

    @Column(name = "connector_type", nullable = false)
    @JsonProperty("connector_type")
    private Integer connectorType;

    @Column(name = "config", nullable = false)
    @Convert(converter = MapStringConverter.class)
    private Map<String, Object> config;

}
