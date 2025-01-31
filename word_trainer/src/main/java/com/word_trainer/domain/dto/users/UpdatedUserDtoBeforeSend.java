package com.word_trainer.domain.dto.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatedUserDtoBeforeSend {
    UserDto dto;
    boolean isReauthenticationRequired;
}
