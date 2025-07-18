package com.haiphamcoder.reporting.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.reporting.domain.model.SourcePermissionComposeKey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Entity
@Table(name = "source_permission")
@IdClass(SourcePermissionComposeKey.class)
public class SourcePermission {

    @Id
    @JsonProperty("source_id")
    private Long sourceId;

    @Id
    @JsonProperty("user_id")
    private Long userId;

    @Column(name = "permission")
    @JsonProperty("permission")
    private String permission;

    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public boolean hasViewPermission() {
        return permission.contains("view");
    }

    public boolean hasEditPermission() {
        return permission.contains("edit");
    }

    public boolean hasOwnerPermission() {
        return permission.contains("owner");
    }

}
