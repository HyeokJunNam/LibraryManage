package com.nhj.librarymanage.security.member;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityUser extends UserDetails {

    Long getId();

    Role getRole();

}