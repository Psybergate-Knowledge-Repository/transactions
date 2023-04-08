package com.psybergate.mentoring.transactions.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CustomerAudit extends BaseEntity{

    public CustomerAudit(final String modifiedBy,
                         final LocalDateTime modifiedDate,
                         final String customerEmail,
                         final String customer,
                         final LocalDateTime createdDate,
                         final LocalDateTime lastModified) {
        super(createdDate, lastModified);
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
        this.customerEmail = customerEmail;
        this.customer = customer;
    }

    private String modifiedBy;

    private LocalDateTime modifiedDate;

    private String customerEmail;

    private String customer;
}
