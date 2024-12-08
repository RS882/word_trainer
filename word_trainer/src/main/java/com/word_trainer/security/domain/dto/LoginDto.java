package com.word_trainer.security.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@Schema(name = "Login data", description = "User credentials")
public class LoginDto {

    @Email(
            message = "Email is not valid",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    @NotNull(message = "Email cannot be null")
    @Schema(description = "User Email", example = "example@gmail.com")
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$",
            message = "Password should include at least one letter (A-Z or a-z)," +
                    " one digit (0-9), one special character (@, #, $, %, ^, &, +, =, !)," +
                    " have no spaces,no less than 8 characters and no more than 20"
    )
    @NotNull(message = "Password cannot be empty")
    @Schema(description = "User password", example = "136Jkn!kPu5%")
    private String password;

    @Override
    public String toString() {
        return String.format("email=%s, password=%s", email, password);
    }

    @JsonCreator
    public LoginDto(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }
}
