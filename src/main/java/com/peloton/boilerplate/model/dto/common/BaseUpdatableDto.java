package com.peloton.boilerplate.model.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUpdatableDto implements Serializable {
    @Schema(name = "데이터 ID (sid) : DB테이블의 PK로 사용", required = false, example = "34918")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Long sid;

    @Schema(name = "데이터 생성 시간", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime insertTime;

    @Schema(name = "데이터 변경 시간", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime updateTime;

    @Schema(name = "데이터 삭제 시간", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime deleteTime;

    @Schema(name = "입력 userId", required = false, example = "jihoon")
    protected String insertId;

    @Schema(name = "수정 userId", required = false, example = "jihoon")
    protected String updateId;

    @Schema(name = "삭제 userId", required = false, example = "jihoon")
    protected String deleteId;
}
