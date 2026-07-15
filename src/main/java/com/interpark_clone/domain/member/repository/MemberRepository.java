package com.interpark_clone.domain.member.repository;

import com.interpark_clone.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberKey(String memberKey);
}
