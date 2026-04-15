package com.nhj.librarymanage.service;

import com.nhj.librarymanage.domain.entity.Member;
import com.nhj.librarymanage.domain.entity.MemberPrincipal;
import com.nhj.librarymanage.repository.MemberRepository;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.AuthenticateFailureException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        Member member = memberRepository.findByLoginId(username).orElseThrow(() -> new AuthenticateFailureException(AuthenticateError.MEMBER_NOT_FOUND));

         return MemberPrincipal.from(member);
    }

}