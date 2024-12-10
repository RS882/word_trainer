package com.word_trainer.services.mapping;

import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-10T10:12:07+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperServiceImpl extends UserMapperService {

    @Override
    public User toEntity(UserRegistrationDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder<?, ?> user = User.builder();

        user.name( dto.getUserName() );
        user.email( dto.getEmail() );

        user.isActive( true );
        user.role( getDefaultRole() );
        user.password( encodePassword(dto) );
        user.loginBlockedUntil( getDefaultLoginBlockedUntil() );

        return user.build();
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder<?, ?> userDto = UserDto.builder();

        userDto.userName( user.getName() );
        userDto.userId( user.getId() );
        userDto.email( user.getEmail() );

        return userDto.build();
    }
}
