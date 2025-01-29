package com.word_trainer.services.mapping;

import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.security.contstants.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "loginBlockedUntil", ignore = true)
    @Mapping(target = "userResult", ignore = true)
    @Mapping(target = "name", expression = "java(updateField(dto.getUserName(), currentUser.getName()))")
    @Mapping(target = "password", expression = "java(updatePassword(dto, currentUser.getPassword()))")
    @Mapping(target = "email", expression = "java(updateField(dto.getEmail(), currentUser.getEmail()))")
    public abstract User toUpdatedEntity(UserUpdateDto dto, @MappingTarget User currentUser);

    protected Role getDefaultRole() {
        return ROLE_USER;
    }

    protected String encodePassword(UserRegistrationDto dto) {
        return encoder.encode(dto.getPassword());
    }

    protected String encodePassword(UserUpdateDto dto) {
        return encoder.encode(dto.getPassword());
    }

    protected LocalDateTime getDefaultLoginBlockedUntil() {
        return LocalDateTime.now();
    }

    protected List<String> getRolesAsStringList(Role role) {
        return Collections.singletonList(role.name());
    }

    protected <T> T updateField(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }

    protected String updatePassword(UserUpdateDto dto, String oldPassword) {
        return dto.getPassword() != null ? encodePassword(dto) : oldPassword;
    }
}
