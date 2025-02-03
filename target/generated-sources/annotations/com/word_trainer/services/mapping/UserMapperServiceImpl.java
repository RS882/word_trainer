package com.word_trainer.services.mapping;

import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import java.util.HashSet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-02T17:38:50+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperServiceImpl extends UserMapperService {

    @Override
    public User toEntity(UserRegistrationDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( dto.getUserName().trim() );
        user.email( dto.getEmail().trim() );
        user.role( getDefaultRole() );
        user.password( encodePassword(dto) );
        user.loginBlockedUntil( getDefaultLoginBlockedUntil() );
        user.userResult( new HashSet<>() );

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

        userDto.roles( getRolesAsStringList(user.getRole()) );

        return userDto.build();
    }

    @Override
    public User toUpdatedEntity(UserUpdateDto dto, User currentUser) {
        if ( dto == null ) {
            return currentUser;
        }

        currentUser.setName( updateField(dto.getUserName(), currentUser.getName()).trim() );
        currentUser.setPassword( updatePassword(dto, currentUser.getPassword()) );
        currentUser.setEmail( updateField(dto.getEmail(), currentUser.getEmail()).trim() );

        return currentUser;
    }
}
