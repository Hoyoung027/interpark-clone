package com.interpark_clone.domain.member.repository;

import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailAndProvider(
            @Param("email") String email,
            @Param("provider") Provider provider
    );

    Optional<Member> findByEmailAndProvider(
            @Param("email") String email,
            @Param("provider") Provider provider
    );
}
