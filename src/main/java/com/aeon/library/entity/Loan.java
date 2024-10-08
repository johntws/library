package com.aeon.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "copy_id")
    private Copy copy;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Date issueDate;
    private Date dueDate;
    private Date returnDate;

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