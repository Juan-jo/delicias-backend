package com.delivery.app.configs.auditable.model;

import com.delivery.app.utils.TimezoneUtil;
import jakarta.persistence.*;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditableEntity {

    @Setter
    @Transient
    private static transient TimezoneUtil timezoneUtil;

    @CreatedDate
    @Column(name = "created_at",nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Column(name = "create_uid")
    @CreatedBy
    protected String createUid;

    @Column(name = "write_uid")
    @LastModifiedBy
    protected String writeUid;


    @PrePersist
    public void prePersist() {
        if (timezoneUtil != null) {
            this.createdAt = ZonedDateTime.now(timezoneUtil.getZoneOffset()).toLocalDateTime();
            this.updatedAt = ZonedDateTime.now(timezoneUtil.getZoneOffset()).toLocalDateTime();
        } else {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (timezoneUtil != null) {
            this.updatedAt = ZonedDateTime.now(timezoneUtil.getZoneOffset()).toLocalDateTime();
        } else {
            this.updatedAt = LocalDateTime.now();
        }
    }

}
