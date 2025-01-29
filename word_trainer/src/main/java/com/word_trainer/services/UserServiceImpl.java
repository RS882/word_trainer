package com.word_trainer.services;


import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.bad_requeat.BadRequestException;
import com.word_trainer.exception_handler.unauthorized.UnauthorizedException;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.services.interfaces.UserService;
import com.word_trainer.services.mapping.UserMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapperService mapper;

    @Override
    @Transactional
    public UserDto createUser(UserRegistrationDto userRegistrationDto) {

        if (userRepository.existsByEmail(userRegistrationDto.getEmail()))
            throw new BadRequestException("Email address already in use");

        User savedUser = saveUser(mapper.toEntity(userRegistrationDto));

        return mapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(UnauthorizedException::new);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDto getCurrentUserInfo(User currentUser) {
        return mapper.toDto(currentUser);
    }

    @Override
    public UserDto updateCurrentUserInfo(UserUpdateDto dto, User currentUser) {
        if (allFieldsUserUpdateDtoNull(dto)) {
            return mapper.toDto(currentUser);
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email address already in use");
        }
        User updatedUser = mapper.toUpdatedEntity(dto, currentUser);
        User savedUser = userRepository.save(updatedUser);
        return mapper.toDto(savedUser);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean allFieldsUserUpdateDtoNull(UserUpdateDto dto) {
        return dto.getPassword() == null &&
                dto.getUserName() == null &&
                dto.getEmail() == null;
    }
}
