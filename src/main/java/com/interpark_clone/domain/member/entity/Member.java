package com.interpark_clone.domain.member.entity;

import com.interpark_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_key", columnNames = "member_key"),
                @UniqueConstraint(name = "uk_provider_email", columnNames = {"provider", "email"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_key", nullable = false, updatable = false, length = 36)
    private String memberKey;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = true, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Builder
    private Member(String email, String password, Provider provider, String providerId) {
        this.memberKey = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER;
        this.status = MemberStatus.ACTIVE;
        this.provider = provider;
        this.providerId = providerId;
    }
}
