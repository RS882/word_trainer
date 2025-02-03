package com.word_trainer.controllers.API;

import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.UserLexemeResultDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "User LexemeResult Controller", description = "Controller for CRUD operation with results user")
@RequestMapping("/v1/users/lexeme-results")
public interface UserLexemeResultAPI {

    @Operation(summary = "Upsert user lexemes results",
            description = "This method create new or update user lexemes results when user is authorized.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserLexemeResultDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User lexemes results upsert successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class))),
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
                                                    "      \"field\": \"UserLexemeResultDto.sourceLanguage\",\n" +
                                                    "      \"message\": \"Source Language cannot be null\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "Source language is wrong",
                                            value = "{\"message\": \"Source language is wrong\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Lexeme not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))
    })
    @PostMapping
    ResponseEntity<ResponseMessageDto> saveOrUpdateUserLexemeResults(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            @NotNull(message = "UserLexemeResultDto can not be null")
            UserLexemeResultDto dto,
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser
    );

    @Operation(summary = "Get user study statistic",
            description = "This method get user study statistic when user is authorized."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User study statistic get successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ResponseUserResultsDto.class))
                    )
            ),
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
                    ))
    })
    @GetMapping
    ResponseEntity<List<ResponseUserResultsDto>> getUserStudyStatistics(
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser
    );
}
