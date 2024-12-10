package com.word_trainer.controllers.API;

import com.word_trainer.domain.dto.lexeme.LexemesFileDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Lexeme Controller", description = "Controller for CRUD operation with lexemes")
@RequestMapping("/v1/lexeme")
public interface LexemeAPI {

    @Operation(summary = "Create new lexemes by file",
            description = "This method create new lexemes from file.",
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
                            }))
    })
    @PostMapping("/file")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<ResponseMessageDto> createLexemesFromFile(
            @ModelAttribute
            @Valid
            LexemesFileDto dto);
}
