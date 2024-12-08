package com.word_trainer.services;


import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.bad_requeat.BadRequestException;
import com.word_trainer.exception_handler.unauthorized.UnauthorizedException;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.services.interfaces.UserService;
import com.word_trainer.services.mapping.UserMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapperService mapper;

    @Override
    public UserDto createUser(UserRegistrationDto userRegistrationDto) {

        if (userRepository.existsByEmail(userRegistrationDto.getEmail()))
            throw new BadRequestException("Email address already in use");

        User savedUser = saveUser(mapper.toEntity(userRegistrationDto));

        return mapper.toDto(savedUser);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(UnauthorizedException::new);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
