package com.word_trainer.controllers.API;

import com.word_trainer.constants.language.Language;
import com.word_trainer.domain.dto.lexeme.LexemeDto;
import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
import com.word_trainer.domain.dto.response.ResponseLexemesDto;
import com.word_trainer.domain.dto.response.ResponseMessageDto;
import com.word_trainer.domain.entity.User;
import com.word_trainer.exception_handler.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lexeme Controller", description = "Controller for CRUD operation with lexemes")
@RequestMapping("/v1/lexeme")
public interface LexemeAPI {

    @Operation(summary = "Create new lexemes by excel file",
            description = "This method creates new lexemes a file when the user is admin. " +
                    "The file must be in the format .xlsx in must contain three columns." +
                    " 1- the source value" +
                    " 2- the value in the target language" +
                    " 3 - the type - can be WORD or PHRASE",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = LexemesFileDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lexemes created successfully",
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
                                                    "      \"field\": \"LexemesFileDto.file\",\n" +
                                                    "      \"message\": \"File cannot be null\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "File format is wrong",
                                            value = "{\"message\": \"File format is wrong\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "403",
                    description = "User doesn't have right for this resource",
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
    @PostMapping("/file")
    ResponseEntity<ResponseMessageDto> createLexemesFromFile(
            @ModelAttribute
            @Valid
            LexemesFileDto dto);

    @Operation(summary = "Create new lexeme",
            description = "This method create new lexeme when user is admin.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LexemeDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lexeme created successfully",
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
                                                    "      \"field\": \"LexemesDto.sourceMeaning\",\n" +
                                                    "      \"message\": \"Source meaning cannot be null\",\n" +
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
            @ApiResponse(responseCode = "403",
                    description = "User doesn't have right for this resource",
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
    ResponseEntity<ResponseMessageDto> createLexeme(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            LexemeDto dto);

    @Operation(summary = "Get lexemes",
            description = "This method get some lexemes when user is authorized. " +
                    "The lexemes are issued taking into account the user's previous" +
                    " actions in studying lexemes "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lexemes get successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class))),
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
                                                    "      \"field\": \"count\",\n" +
                                                    "      \"message\": \"count is wrong\",\n" +
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
    @GetMapping
    ResponseEntity<ResponseLexemesDto> getLexemes(
            @Valid
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Count of lexeme in response. Minimum value is 1 , maximum is 50", example = "10")
            @Min(1)
            @Max(50)
            int count,
            @Valid
            @Parameter(description = "Source language.", example = "EN")
            @NotNull(message = "Source language cannot be null")
            Language sourceLanguage,
            @Valid
            @Parameter(description = "Target language.", example = "DE")
            @NotNull(message = "Target language cannot be null")
            Language targetLanguage,
            @AuthenticationPrincipal
            @Parameter(hidden = true)
            User currentUser
    );
}
