package com.word_trainer.controllers;


import com.word_trainer.controllers.API.UserAPI;
import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDto> createUser(UserRegistrationDto userRegistrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userRegistrationDto));
    }

    @Override
    public ResponseEntity<UserDto> getMeInfo(User currentUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.geCurrentUserInfo(currentUser));
    }

}
