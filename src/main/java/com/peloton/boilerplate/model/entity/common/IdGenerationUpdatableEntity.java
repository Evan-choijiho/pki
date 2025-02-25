package com.peloton.boilerplate.model.entity.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // 사용자의 정보 조회 후 DB 등록자와 수정자로 활용
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
    @CreatedBy
    @Column(updatable = false)
    protected String insertId;
    @LastModifiedBy
    protected String updateId;
    @Column
    protected String deleteId;
}
