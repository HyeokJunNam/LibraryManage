package com.nhj.librarymanage.security.member;

public interface AuthenticatedUser {

    Long getId();

    String getLoginId();

    String getName();

}
