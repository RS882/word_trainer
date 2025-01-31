package com.word_trainer.services.interfaces;


import com.word_trainer.domain.dto.users.UpdatedUserDtoBeforeSend;
import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;

public interface UserService {
    UserDto createUser(UserRegistrationDto userRegistrationDto);

    User getUserByEmail(String email);

    User saveUser(User user);

    UserDto getCurrentUserInfo(Long currentUserId);

    UpdatedUserDtoBeforeSend updateCurrentUserInfo(UserUpdateDto userUpdateDto, Long currentUserId);

    boolean existsUserByEmail(String email);
}
