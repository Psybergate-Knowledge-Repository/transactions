package com.psybergate.mentoring.transactions.spring.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @PrePersist
    public void setInfo(){
        this.createdDate = createdDate != null ? createdDate : LocalDateTime.now();
        this.lastModified = lastModified != null ? lastModified : LocalDateTime.now();
    }
}