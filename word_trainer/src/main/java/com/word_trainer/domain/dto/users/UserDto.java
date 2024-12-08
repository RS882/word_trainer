package com.word_trainer.domain.dto.users;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO with user information")
public class UserDto {

    @Schema(description = "User id", example = "28")
    @Min(value = 0L, message = "User id must be positive")
    private Long userId;

    @Schema(description = "User name", example = "John")
    private String userName;

    @Schema(description = "User email", example = "example@gmail.com")
    private String email;
}
