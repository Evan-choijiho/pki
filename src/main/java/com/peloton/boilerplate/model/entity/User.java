package com.peloton.boilerplate.model.entity;

import com.peloton.boilerplate.model.entity.common.IdGenerationUpdatableEntity;
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
    private String name;
    @Column
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    @Enumerated(EnumType.STRING)
    private Grant grant;
    @Column
    private String regType;

}
