package com.word_trainer.services.mapping;

import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.security.contstants.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.word_trainer.security.contstants.Role.ROLE_USER;


@Mapper(imports = {java.util.HashSet.class})
public abstract class UserMapperService {

    @Autowired
    protected PasswordEncoder encoder;

    @Mapping(target = "name", source = "userName")
    @Mapping(target = "role", expression = "java(getDefaultRole())")
    @Mapping(target = "password", expression = "java(encodePassword(dto))")
    @Mapping(target = "loginBlockedUntil", expression = "java(getDefaultLoginBlockedUntil())")
    @Mapping(target = "userResult", expression = "java(new HashSet<>())")
    public abstract User toEntity(UserRegistrationDto dto);

    @Mapping(target = "userName", source = "name")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "roles", expression = "java(getRolesAsStringList(user.getRole()))")
    public abstract UserDto toDto(User user);

    protected Role getDefaultRole() {
        return ROLE_USER;
    }

    protected String encodePassword(UserRegistrationDto dto) {
        return encoder.encode(dto.getPassword());
    }

    protected LocalDateTime getDefaultLoginBlockedUntil() {
        return LocalDateTime.now();
    }

    protected List<String> getRolesAsStringList(Role role) {
        return Collections.singletonList(role.name());
    }
}
