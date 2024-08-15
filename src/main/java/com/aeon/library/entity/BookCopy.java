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
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private Book book;

    @CreationTimestamp
    private Timestamp createdDate;

    private String createdBy;

    @UpdateTimestamp
    private Timestamp modifiedDate;

    private String modifiedBy;

    private boolean deleted;

    private boolean borrowed;

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
