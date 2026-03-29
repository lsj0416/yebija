package com.yebija.auth.service;

import com.yebija.auth.dto.LoginRequest;
import com.yebija.auth.dto.SignupRequest;
import com.yebija.auth.dto.TokenResponse;
import com.yebija.auth.jwt.JwtProperties;
import com.yebija.auth.jwt.JwtProvider;
import com.yebija.church.domain.Church;
import com.yebija.church.domain.enums.Denomination;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ChurchRepository churchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (churchRepository.existsByAdminEmail(request.getAdminEmail())) {
            throw new YebijaException(ErrorCode.DUPLICATE_EMAIL);
        }

        Denomination denomination;
        try {
            denomination = Denomination.valueOf(request.getDenomination().toUpperCase());
        } catch (IllegalArgumentException e) {
            denomination = Denomination.OTHER;
        }

        Church church = Church.create(
                request.getName(),
                denomination,
                request.getAdminEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        church = churchRepository.save(church);

        return issueTokens(church);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Church church = churchRepository.findByAdminEmail(request.getAdminEmail())
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), church.getPasswordHash())) {
            throw new YebijaException(ErrorCode.INVALID_PASSWORD);
        }

        return issueTokens(church);
    }

    private TokenResponse issueTokens(Church church) {
        String accessToken = jwtProvider.generateAccessToken(church.getId(), church.getAdminEmail());
        String refreshToken = jwtProvider.generateRefreshToken(church.getId());
        return new TokenResponse(accessToken, jwtProperties.getAccessExpiration(), refreshToken);
    }
}
