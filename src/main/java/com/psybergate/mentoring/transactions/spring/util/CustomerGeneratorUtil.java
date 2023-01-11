package com.psybergate.mentoring.transactions.spring.util;

import com.psybergate.mentoring.transactions.spring.dto.Customer;

import java.util.Random;

public class CustomerGeneratorUtil {

    public static Customer generateRandomCustomer() {
        final String name = "Hickory" + Math.random()*10E5;
        final String surname = "Dickory" + Math.random()*10E5;
        final String email = name + "." + surname + "@gmail.com";
        final String phoneNumber = String.valueOf(Double.valueOf(Math.random()).longValue());
        return new Customer(name, surname, email, phoneNumber);
    }

}
