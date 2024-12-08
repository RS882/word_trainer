package com.word_trainer.security.services.interfaces;


import com.word_trainer.security.domain.AuthInfo;
import io.jsonwebtoken.Claims;

public interface AuthInfoService {

    AuthInfo mapClaims(Claims claims);
}
