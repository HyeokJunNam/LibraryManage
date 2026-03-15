package com.nhj.librarymanage.security.member.dao;

import com.nhj.librarymanage.security.member.SecurityUser;

import java.util.Optional;

public interface MemberDataAccess {

    Optional<SecurityUser> retrieveUser(String loginId);

    void updateUser(SecurityUser securityUser);

}