package com.word_trainer.security.services;


import com.word_trainer.domain.entity.User;
import com.word_trainer.security.domain.AuthInfo;
import com.word_trainer.security.services.interfaces.AuthInfoService;
import com.word_trainer.services.interfaces.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.word_trainer.security.services.TokenService.USER_EMAIL_VARIABLE_NAME;
import static com.word_trainer.security.services.TokenService.USER_NAME_VARIABLE_NAME;

@Service
@RequiredArgsConstructor
public class AuthInfoServiceImpl implements AuthInfoService {

    private final UserService userService;

    @Override
    public AuthInfo mapClaims(Claims claims) {

        String userEmail = claims.getSubject();

        if (userService.existsUserByEmail(userEmail)) {
            User currentUser = userService.getUserByEmail(userEmail);

            if (isClaimsCompatibleWithUserData(claims, currentUser)) {
                return new AuthInfo(currentUser);
            }
        }
        return null;
    }

    private boolean isClaimsCompatibleWithUserData(Claims claims, User user) {
        if (user == null) return false;
        String userNameFromClams = (String) claims.get(USER_NAME_VARIABLE_NAME);
        String userEmailFromClams = (String) claims.get(USER_EMAIL_VARIABLE_NAME);
        return userNameFromClams.equals(user.getName()) && userEmailFromClams.equals(user.getEmail());
    }
}
