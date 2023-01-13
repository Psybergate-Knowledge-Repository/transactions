package com.psybergate.mentoring.transactions.jdbc.util;


import com.github.javafaker.Faker;
import com.psybergate.mentoring.transactions.jdbc.dto.Customer;

public class CustomerGeneratorUtil {

    public static final Faker FAKER = new Faker();

    public static Customer generateRandomCustomer() {
        final String name = FAKER.name().firstName();
        final String surname = FAKER.name().lastName();
        final String email = name + "." + surname + "@gmail.com";
        final String phoneNumber = String.valueOf(Double.valueOf(Math.random()*10E9).longValue());
        return new Customer(name, surname, email, phoneNumber);
    }

    private static int getRandomInt() {
        return (int) (Math.random() * 10E5);
    }

}
