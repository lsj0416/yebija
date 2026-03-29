package com.yebija.auth.security;

import com.yebija.church.domain.Church;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ChurchRepository churchRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Church church = churchRepository.findByAdminEmail(email)
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));
        return new CustomUserDetails(church.getId(), church.getAdminEmail());
    }
}
