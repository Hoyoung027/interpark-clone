package com.interpark_clone.global.security;

import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.member.repository.MemberRepository;
import com.interpark_clone.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.interpark_clone.global.code.BusinessErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String memberKey) {
        return loadUserByMemberKey(memberKey);
    }

    public CustomUserDetails loadUserByMemberKey(String memberKey) {

        Member member = memberRepository.findByMemberKey(memberKey)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        return CustomUserDetails.builder()
                .memberId(member.getId())
                .memberKey(member.getMemberKey())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }

}
