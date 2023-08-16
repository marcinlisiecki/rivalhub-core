package com.rivalhub.security;

import com.rivalhub.user.UserData;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    static public UserData getUserFromSecurityContext(){
        return (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
