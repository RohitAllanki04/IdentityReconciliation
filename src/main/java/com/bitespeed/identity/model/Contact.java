package com.bitespeed.identity.model;

import jakarta.persistence.*;
        import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact")
@Data
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;

    private String email;

    private Long linkedId;

    @Enumerated(EnumType.STRING)
    private LinkPrecedence linkPrecedence;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public enum LinkPrecedence {
        primary, secondary
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Contact(String email, String phoneNumber, Long linkedId, LinkPrecedence precedence) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.linkedId = linkedId;
        this.linkPrecedence = precedence;
    }
}