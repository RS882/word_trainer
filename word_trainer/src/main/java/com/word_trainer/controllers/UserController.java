package com.word_trainer.controllers;


import com.word_trainer.controllers.API.UserAPI;
import com.word_trainer.domain.dto.users.UpdatedUserDtoBeforeSend;
import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.security.services.CookieService;
import com.word_trainer.security.services.interfaces.AuthService;
import com.word_trainer.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

    private final UserService userService;

    private final CookieService cookieService;

    private final AuthService authService;

    @Override
    public ResponseEntity<UserDto> createUser(UserRegistrationDto userRegistrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRegistrationDto));
    }

    @Override
    public ResponseEntity<UserDto> getMeInfo(User currentUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getCurrentUserInfo(currentUser.getId()));
    }

    @Override
    public ResponseEntity<UserDto> updateMeInfo(UserUpdateDto userUpdateDto,
                                                User currentUser,
                                                HttpServletResponse response,
                                                String refreshToken,
                                                String accessToken) {
        UpdatedUserDtoBeforeSend updatedUserDto = userService.updateCurrentUserInfo(userUpdateDto, currentUser.getId());
        if (updatedUserDto.isReauthenticationRequired()) {
            authService.logout(refreshToken, accessToken);
            cookieService.removeRefreshTokenFromCookie(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedUserDto.getDto());
    }
}
