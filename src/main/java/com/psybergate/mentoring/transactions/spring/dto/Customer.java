package com.psybergate.mentoring.transactions.spring.dto;


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
