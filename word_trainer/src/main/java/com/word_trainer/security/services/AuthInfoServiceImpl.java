package com.word_trainer.security.services;


import com.word_trainer.domain.entity.User;
import com.word_trainer.security.domain.AuthInfo;
import com.word_trainer.security.services.interfaces.AuthInfoService;
import com.word_trainer.services.interfaces.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthInfoServiceImpl implements AuthInfoService {

    private final UserService userService;

    @Override
    public AuthInfo mapClaims(Claims claims) {

        String userEmail = claims.getSubject();

        User currentUser = userService.getUserByEmail(userEmail);
        return new AuthInfo(currentUser);
    }
}
