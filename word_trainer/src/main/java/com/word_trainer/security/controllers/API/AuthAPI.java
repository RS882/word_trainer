package com.word_trainer.security.controllers.API;


import com.word_trainer.configs.annotations.bearer_token.BearerToken;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.security.domain.dto.LoginDto;
import com.word_trainer.security.domain.dto.TokenResponseDto;
import com.word_trainer.security.domain.dto.ValidationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.word_trainer.security.services.AuthServiceImpl.MAX_COUNT_OF_LOGINS;
import static com.word_trainer.security.services.CookieService.COOKIE_REFRESH_TOKEN_NAME;


@RequestMapping("/v1/auth")
@Tag(name = "Authentication controller", description = "Controller for User authentication using JWT")
public interface AuthAPI {


    @Operation(
            summary = "Login for Users and set refresh token in cookie",
            description = "Authenticates a user and returns a tokens. "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful login",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponseDto.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Incorrect password or email",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)

                    )),
            @ApiResponse(responseCode = "403",
                    description = "Count of user's logins is more than maximum(" + MAX_COUNT_OF_LOGINS + ")",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))}
    )
    @PostMapping("/login")
    ResponseEntity<TokenResponseDto> login(
            @Valid
            @Parameter(description = "Login DTO")
            @RequestBody
            LoginDto loginDto,
            @Parameter(hidden = true)
            HttpServletResponse response);


    @Operation(
            summary = "Refresh user's access and refresh token",
            description = "Refresh user's access and refresh token and returns a tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful refresh",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponseDto.class))}
            ),
            @ApiResponse(responseCode = "400",
                    description = "Cookie is incorrect",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Invalid token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))}
    )
    @GetMapping("/refresh")
    ResponseEntity<TokenResponseDto> refresh(
            @Parameter(hidden = true)
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
            String refreshToken);

    @Operation(
            summary = "Validation of user's access token",
            description = "Validation of user's access bearer token in header authorization" +
                    " and returns validation information.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful validation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationResponseDto.class))}
            ),
            @ApiResponse(responseCode = "401",
                    description = "Invalid token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))})
    @GetMapping("/validation")
    ResponseEntity<ValidationResponseDto> validation(
            @Parameter(hidden = true)
            @BearerToken
            @NotNull
            String accessToken);

    @Operation(
            summary = "Logout of user",
            description = "Logout of user. Remove the refresh token from cookie and database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful logout"
            ),
            @ApiResponse(responseCode = "401",
                    description = "Invalid token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))})
    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void logout(
            @Parameter(hidden = true)
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
            @BearerToken
            @Parameter(hidden = true)
            @NotNull
            String accessToken);
}
