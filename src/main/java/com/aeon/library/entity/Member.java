package com.aeon.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @CreationTimestamp
    private Timestamp createdDate;
    private String createdBy;

    @CreationTimestamp
    private Timestamp modifiedDate;
    private String modifiedBy;
    private boolean deleted;

    @PrePersist
    public void prePersist() {
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }

        if (modifiedBy == null) {
            modifiedBy = "SYSTEM";
        }
    }
}