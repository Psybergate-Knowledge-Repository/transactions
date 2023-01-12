package com.psybergate.mentoring.transactions.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BaseEntity {

    private LocalDateTime createdDate;

    private LocalDateTime lastModified;

    public BaseEntity() {
        setDateInfo();
    }

    public BaseEntity(final LocalDateTime createdDate,
                      final LocalDateTime lastModified) {
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }

    public void setDateInfo(){
        this.createdDate = createdDate != null ? createdDate : LocalDateTime.now();
        this.lastModified = lastModified != null ? lastModified : LocalDateTime.now();
    }
}