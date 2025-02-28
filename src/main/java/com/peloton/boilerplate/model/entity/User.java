package com.peloton.boilerplate.model.entity;

import com.peloton.boilerplate.model.dto.request.SignUpDto;
import com.peloton.boilerplate.model.entity.common.IdGenerationUpdatableEntity;
import com.peloton.boilerplate.service.common.EncryptConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends IdGenerationUpdatableEntity {

    public enum Status {
        active, expiration, pending, temp
    }
    public enum Grant {
        normal, admin, superAdmin
    }

    @Column
    private String userId;
    @Column
    @Convert(converter = EncryptConverter.class) // 성명 암호화
    private String name;
    @Column
    private String password;
    @Column
    @Convert(converter = EncryptConverter.class) // 연락처 암호화
    private String phone;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private Status status;
//    @Column
//    @Enumerated(EnumType.STRING)
//    private Grant grant;

    public void regEntity (SignUpDto signUpDto, String password) {
        this.userId = signUpDto.getUserId();
        this.password = password;
        this.name = signUpDto.getName();
        this.phone = signUpDto.getPhone();
    }

}
