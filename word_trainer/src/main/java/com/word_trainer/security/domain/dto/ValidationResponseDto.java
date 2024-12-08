package com.word_trainer.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(name = "Information after validation", description = "Information after validation successful validation")
public class ValidationResponseDto {
    @Schema(description = "User id", example = "234")
    private Long userId;
    @Schema(description = "Is user authorized", example = "true")
    Boolean isAuthorized;
    @Schema(description = "Roles of user", example = "[USER_ROLE]")
    List<String> roles;
}
