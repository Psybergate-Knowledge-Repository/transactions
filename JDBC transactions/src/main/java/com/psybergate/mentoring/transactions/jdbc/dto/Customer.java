package com.psybergate.mentoring.transactions.jdbc.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Customer {

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;
}
