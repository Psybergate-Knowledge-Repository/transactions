package com.psybergate.mentoring.transactions.jdbc.dto;


import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Customer extends BaseEntity{

    public Customer(final LocalDateTime createdDate,
                    final LocalDateTime lastModified,
                    final String name,
                    final String surname,
                    final String email,
                    final String phoneNumber) {
        super(createdDate, lastModified);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;
}
