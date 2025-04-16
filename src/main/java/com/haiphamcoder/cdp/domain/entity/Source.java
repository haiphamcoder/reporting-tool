package com.haiphamcoder.cdp.domain.entity;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haiphamcoder.cdp.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "source")
public class Source extends BaseEntity {
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

    @Column(name = "type_connector", nullable = false)
    private Integer typeConnector;

    @Column(name = "config", nullable = false)
    private String config;

    @Column(name = "status", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonProperty("user")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "source_share", joinColumns = @JoinColumn(name = "source_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonProperty("shared_users")
    private List<User> sharedUsers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonProperty("folder")
    private Folder folder;

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
    private Timestamp lastSyncTime;

}