package com.interpark_clone.domain.exhibition.dto.request;

import com.interpark_clone.domain.venue.entity.City;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record RegionalExhibitionRequest(
        @Parameter(
                description = "조회할 지역입니다.",
                required = true,
                schema = @Schema(type = "string", allowableValues = {
                        "SEOUL", "GYEONGGI", "INCHEON", "GYEONGNAM", "BUSAN", "GYEONGBUK", "DAEGU", "JEONNAM",
                        "GWANGJU", "JEONBUK", "CHUNGNAM", "DAEJEON", "CHUNGBUK", "GANGWON", "JEJU", "ULSAN"
                })
        )
        @NotNull(message = "region은 필수입니다.")
        City region,
        @Schema(description = "페이지 번호입니다. 0부터 시작합니다.", defaultValue = "0", minimum = "0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Schema(description = "페이지 크기입니다.", defaultValue = "20", minimum = "1", maximum = "100", example = "20")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public RegionalExhibitionRequest {
        page = page == null ? DEFAULT_PAGE : page;
        size = size == null ? DEFAULT_SIZE : size;
    }
}
