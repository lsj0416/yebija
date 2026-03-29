package com.yebija.common.util;

import com.yebija.auth.security.CustomUserDetails;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static Long getCurrentChurchId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new YebijaException(ErrorCode.UNAUTHORIZED);
        }
        return ((CustomUserDetails) auth.getPrincipal()).getChurchId();
    }
}
