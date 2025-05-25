package com.haiphamcoder.reporting.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "source")
public class Source {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty("id")
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "description", nullable = true)
    @JsonProperty("description")
    @Builder.Default
    private String description = "";

    @Column(name = "connector_type", nullable = false)
    private Integer connectorType;

    @Column(name = "mapping", nullable = true)
    @Builder.Default
    private String mapping = "";

    @Column(name = "config", nullable = true)
    @Builder.Default
    private String config = "";

    @Column(name = "table_name", nullable = true)
    @Builder.Default
    private String tableName = "";

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @JsonProperty("is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_starred", nullable = false)
    @Builder.Default
    @JsonProperty("is_starred")
    private Boolean isStarred = false;

    @Column(name = "last_sync_time", nullable = true)
    @JsonProperty("last_sync_time")
    @Builder.Default
    private LocalDateTime lastSyncTime = LocalDateTime.now();

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

}