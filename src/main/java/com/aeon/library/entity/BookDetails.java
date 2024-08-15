package com.aeon.library.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
public class BookDetails {
    @Id
    private String isbn;

    private String title;
    private String author;
    private boolean returned;

    @CreationTimestamp
    private Timestamp createdDate;

    private String createdBy;

    @UpdateTimestamp
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
