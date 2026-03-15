package com.nhj.librarymanage.security.member;

public interface SecurityUser {

    Long getId();

    String getLoginId();

    String getPassword();

    Role getRole();

}