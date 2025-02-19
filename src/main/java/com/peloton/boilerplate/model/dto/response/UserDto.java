package com.peloton.boilerplate.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peloton.boilerplate.model.dto.common.BaseUpdatableDto;
import com.peloton.boilerplate.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@Schema(description = "사용자 정보 DTO")
public class UserDto extends BaseUpdatableDto {
    @Schema(name = "사용자 id", required = false, example = "hansener")
    private String userId;
    @Schema(name = "사용자명", required = false, example = "지훈")
    private String name;

    @Schema(name = "company sid", required = false, example = "100001")
    private Long companySid;

    @Schema(name = "group sid", required = false, example = "100001")
    private Long groupSid;

    @Schema(name = "group명", required = false, example = "IT 개발본부")
    private String groupName;

    @Schema(name = "비밀번호", required = false, example = "!@#34234!@24")
    private String password;

    @Schema(name = "유저상태", required = false, example = "active")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User.Status status;

    @Schema(name = "권한", required = false, example = "normal")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User.Grant grant;


    public UserDto(User user) {
        if( user != null ) {
            // common
            this.sid = user.getSid();
            this.insertTime = user.getInsertTime();
            this.updateTime = user.getUpdateTime();
            this.deleteTime = user.getDeleteTime();
            this.insertId = user.getInsertId();
            this.updateId = user.getUpdateId();
            this.deleteId = user.getDeleteId();

            // own
            this.userId = user.getUserId();
            this.name = user.getName();
            this.status = user.getStatus();
            this.grant = user.getGrant();
        }
    }

}
