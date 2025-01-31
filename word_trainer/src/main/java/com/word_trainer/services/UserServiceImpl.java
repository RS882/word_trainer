package com.word_trainer.services;


import com.word_trainer.domain.dto.users.UpdatedUserDtoBeforeSend;
import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.bad_requeat.BadRequestException;
import com.word_trainer.exception_handler.not_found.exceptions.UserNotFoundException;
import com.word_trainer.exception_handler.unauthorized.UnauthorizedException;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.services.interfaces.UserService;
import com.word_trainer.services.mapping.UserMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    @Transactional
    public UserDto getCurrentUserInfo(Long currentUserId) {
        User currentUser = getUserById(currentUserId);
        return mapper.toDto(currentUser);
    }

    @Override
    @Transactional
    public UpdatedUserDtoBeforeSend updateCurrentUserInfo(UserUpdateDto dto, Long currentUserId) {
        if (StringUtils.hasText(dto.getEmail()) && existsUserByEmail(dto.getEmail().trim())) {
            throw new BadRequestException("Email address already in use");
        }
        User currentUser = getUserById(currentUserId);

        if (allFieldsUserUpdateDtoNull(dto)) {
            return UpdatedUserDtoBeforeSend.builder()
                    .dto(mapper.toDto(currentUser))
                    .build();
        }
        String newEmail = dto.getEmail() != null ? dto.getEmail().trim() : null;
        boolean emailChanged = newEmail != null && !currentUser.getEmail().equalsIgnoreCase(newEmail);
        boolean passwordChanged = dto.getPassword() != null;

        mapper.toUpdatedEntity(dto, currentUser);

        return UpdatedUserDtoBeforeSend.builder()
                .dto(mapper.toDto(currentUser))
                .isReauthenticationRequired(emailChanged || passwordChanged)
                .build();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private boolean allFieldsUserUpdateDtoNull(UserUpdateDto dto) {
        return dto.getPassword() == null &&
                dto.getUserName() == null &&
                dto.getEmail() == null;
    }
}
