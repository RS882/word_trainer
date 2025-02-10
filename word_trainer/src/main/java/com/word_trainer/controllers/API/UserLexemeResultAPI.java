package com.word_trainer.controllers.API;

import com.word_trainer.constants.language.Language;
import com.word_trainer.controllers.validators.ValidEnum;
import com.word_trainer.domain.dto.response.PageResponseUserResultsTranslationDto;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.dto.response.ResponseUserResultsDto;
import com.word_trainer.domain.dto.user_lexeme_result.UpdateStatusUserLexemeResultDto;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User LexemeResult Controller", description = "Controller for CRUD operation with results user")
@RequestMapping("/v1/users/lexeme-results")
public interface UserLexemeResultAPI {

    String PAGE_VALUE = "0";
    String SIZE_VALUE = "10";
    String SORT_BY = "attempts";

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

    @Operation(summary = "Get user translation results by languages ",
            description = "This method get user translation results by languages when user is authorized."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User translation results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseUserResultsTranslationDto.class)
                    )
            ),
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
                                                    "      \"field\": \"SourceLanguage\",\n" +
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
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    ))
    })
    @GetMapping("/translations")
    ResponseEntity<PageResponseUserResultsTranslationDto> getTranslations(
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser,

            @RequestParam
            @Parameter(description = "Source language for lexeme", example = "EN")
            @NotNull(message = "Source language cannot be null")
            @ValidEnum(enumClass = Language.class, message = "Invalid language code")
            String sourceLanguage,

            @RequestParam
            @Parameter(description = "Target language for lexeme", example = "DE")
            @NotNull(message = "Target language cannot be null")
            @ValidEnum(enumClass = Language.class, message = "Invalid language code")
            String targetLanguage,

            @RequestParam(defaultValue = PAGE_VALUE)
            @Parameter(description = "Requested page number.", example = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = SIZE_VALUE)
            @Parameter(description = "Number of entities per page.", example = "10")
            @Min(1)
            int size,

            @RequestParam(defaultValue = SORT_BY)
            @Parameter(description = "Sorting field.", examples = {
                    @ExampleObject(name = "Sort by attempts (default)", value = "attempts"),
                    @ExampleObject(name = "Sort by type", value = "type"),
                    @ExampleObject(name = "Sort by successful attempts", value = "successfulAttempts"),
                    @ExampleObject(name = "Sort by is Active", value = "isActive")
            })
            String sortBy,

            @RequestParam(defaultValue = "true")
            @Parameter(description = "Sorting direction.", examples = {
                    @ExampleObject(name = "Sort direction is ascending (default)", value = "true"),
                    @ExampleObject(name = "Sort direction is descending", value = "false")
            })
            Boolean isAsc
    );

    @Operation(summary = "Update user lexemes results status ",
            description = "This method update user lexemes results status when user is authorized."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User translation results retrieved successfully"
            ),
            @ApiResponse(responseCode = "400", description = "Request is wrong",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    oneOf = {
                                            ValidationErrorsDto.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Errors",
                                            value = "{\n" +
                                                    "  \"errors\": [\n" +
                                                    "    {\n" +
                                                    "      \"field\": \"lexemeId\",\n" +
                                                    "      \"message\": \"Source Language cannot be null\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
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
                    ))
    })
    @PatchMapping("/active")
    ResponseEntity<Void> updateUserLexemeResultStatus(
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser,

            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            @NotNull(message = "Dto list cannot be null")
            @Size(min = 1, message = "Dto list cannot be empty")
            List<@Valid UpdateStatusUserLexemeResultDto> dto
    );
}
