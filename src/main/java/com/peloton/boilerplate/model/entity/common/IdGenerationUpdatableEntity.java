package com.peloton.boilerplate.model.entity.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class IdGenerationUpdatableEntity {
    @Id
    @Column(name = "sid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long sid;
    @Column(name = "insert_time", insertable = false, updatable = false)
    protected LocalDateTime insertTime;
    @Column(name = "update_time", insertable = false, updatable = false)
    protected LocalDateTime updateTime;
    @Column(name = "delete_time")
    protected LocalDateTime deleteTime;
    @Column
    protected String insertId;
    @Column
    protected String updateId;
    @Column
    protected String deleteId;
}
