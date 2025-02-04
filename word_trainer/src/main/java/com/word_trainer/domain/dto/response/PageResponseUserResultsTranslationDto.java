package com.word_trainer.domain.dto.response;

import com.word_trainer.domain.dto.user_lexeme_result.ResponseUserResultsTranslationDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Schema(description = "Paginated response")
public class PageResponseUserResultsTranslationDto {

    @Schema(description = "Current page number", example = "0")
    private int pageNumber;

    @Schema(description = "Number of elements per page", example = "10")
    private int pageSize;

    @Schema(description = "Total number of elements", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    @Schema(description = "Is this the fist page?", example = "true")
    private boolean first;

    @Schema(description = "Translation results")
    @ArraySchema(schema = @Schema(implementation = ResponseUserResultsTranslationDto.class))
    private List<ResponseUserResultsTranslationDto> content;

    public PageResponseUserResultsTranslationDto(Page<ResponseUserResultsTranslationDto> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.content = page.getContent();
        this.first =page.isFirst();
    }
}

