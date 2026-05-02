package com.project.ticket.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(length = 255)
    private String profileImage;

    @Column(nullable = false)
    private boolean deleted;

    protected User() {
    }

    public static User createFan(String name) {
        User user = new User();
        user.name = name;
        user.role = UserRole.FAN;
        user.deleted = false;
        return user;
    }

    public static User createArtist(String name) {
        User user = new User();
        user.name = name;
        user.role = UserRole.ARTIST;
        user.deleted = false;
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
