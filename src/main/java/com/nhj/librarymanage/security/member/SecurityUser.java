package com.nhj.librarymanage.security.member;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityUser extends UserDetails {

    String getName();

    Long getId();

    Role getRole();

}