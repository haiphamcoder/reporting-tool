package com.haiphamcoder.reporting.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.reporting.domain.model.ReportPermissionComposeKey;

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
@Table(name = "report_permission")
@IdClass(ReportPermissionComposeKey.class)
public class ReportPermission extends BaseEntity {

    @Id
    @JsonProperty("report_id")
    private Long reportId;

    @Id
    @JsonProperty("user_id")
    private Long userId;

    @Column(name = "permission")
    @JsonProperty("permission")
    private String permission;

    public boolean hasReadPermission() {
        return permission.contains("r");
    }

    public boolean hasWritePermission() {
        return permission.contains("w");
    }

    public boolean hasExecutePermission() {
        return permission.contains("x");
    }
}