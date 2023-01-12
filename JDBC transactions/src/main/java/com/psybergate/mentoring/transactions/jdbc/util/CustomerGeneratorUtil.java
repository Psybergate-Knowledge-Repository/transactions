package com.psybergate.mentoring.transactions.jdbc.util;


import com.psybergate.mentoring.transactions.jdbc.dto.Customer;

public class CustomerGeneratorUtil {

    public static Customer generateRandomCustomer() {
        final String name = "Hickory" + getRandomInt();
        final String surname = "Dickory" + getRandomInt();
        final String email = name + "." + surname + "@gmail.com";
        final String phoneNumber = String.valueOf(Double.valueOf(Math.random()*10E9).longValue());
        return new Customer(name, surname, email, phoneNumber);
    }

    private static int getRandomInt() {
        return (int) (Math.random() * 10E5);
    }

}
