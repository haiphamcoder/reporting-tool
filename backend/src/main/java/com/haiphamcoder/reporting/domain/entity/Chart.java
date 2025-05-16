package com.haiphamcoder.reporting.domain.entity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.reporting.shared.BaseEntity;
import com.haiphamcoder.reporting.shared.converter.JsonNodeStringConverter;
import com.haiphamcoder.reporting.shared.converter.MapStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Chart extends BaseEntity {

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
    @Convert(converter = MapStringConverter.class)
    private Map<String, Object> config;

    @Column(name = "query_option", nullable = false)
    @Convert(converter = JsonNodeStringConverter.class)
    private JsonNode queryOption;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chart_report", joinColumns = @JoinColumn(name = "chart_id"), inverseJoinColumns = @JoinColumn(name = "report_id"))
    @JsonProperty("reports")
    private List<Report> reports;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chart_permission", joinColumns = @JoinColumn(name = "chart_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonProperty("shared_users")
    private List<User> sharedUsers;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @JsonProperty("is_deleted")
    private Boolean isDeleted = false;

}
