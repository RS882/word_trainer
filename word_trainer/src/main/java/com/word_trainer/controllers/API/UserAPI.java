package com.word_trainer.controllers.API;


import com.word_trainer.configs.annotations.bearer_token.BearerToken;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.dto.users.UserDto;
import com.word_trainer.domain.dto.users.UserRegistrationDto;
import com.word_trainer.domain.dto.users.UserUpdateDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.word_trainer.security.services.CookieService.COOKIE_REFRESH_TOKEN_NAME;

@Tag(name = "User Controller", description = "Controller for CRUD operation with user")
@RequestMapping("/v1/users")
public interface UserAPI {

    @Operation(summary = "Create new user",
            description = "This method create new user from userDto.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserRegistrationDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Request is wrong",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    oneOf = {
                                            ValidationErrorsDto.class,
                                            ResponseMessageDto.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Errors",
                                            value = "{\n" +
                                                    "  \"errors\": [\n" +
                                                    "    {\n" +
                                                    "      \"field\": \"UserRegistrationDto.userName\",\n" +
                                                    "      \"message\": \"Username must be between 3 and 20 characters\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "Wrong email",
                                            value = "{\"message\": \"Email address already in use\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
    })
    @PostMapping("/registration")
    ResponseEntity<UserDto> createUser(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            UserRegistrationDto userRegistrationDto);

    @Operation(summary = "Get information about the current user.",
            description = "This method get general information about the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users information sent successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
    })
    @GetMapping("/me")
    ResponseEntity<UserDto> getMeInfo(
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser
    );

    @Operation(summary = "Update current user information",
            description = "This method update current user information when user is authorized.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserUpdateDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"
                    , description = "User information update successfully. No need to log in again",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "205"
                    , description = "User information update successfully. Log in again",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Request is wrong",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    oneOf = {
                                            ValidationErrorsDto.class,
                                            ResponseMessageDto.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Errors",
                                            value = "{\n" +
                                                    "  \"errors\": [\n" +
                                                    "    {\n" +
                                                    "      \"field\": \"UserUpdateDto.userName\",\n" +
                                                    "      \"message\": \"Username must be between 3 and 20 characters\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "Wrong email",
                                            value = "{\"message\": \"Email address already in use\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
    })
    @PutMapping("/me")
    ResponseEntity<Void> updateMeInfo(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            UserUpdateDto userUpdateDto,
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser,
            HttpServletResponse response,
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = COOKIE_REFRESH_TOKEN_NAME,
                    required = true,
                    hidden = true,
                    schema = @Schema(type = "string")
            )
            @CookieValue(name = COOKIE_REFRESH_TOKEN_NAME)
            @NotNull
            String refreshToken,
            @Parameter(hidden = true)
            @BearerToken String accessToken
    );

}
