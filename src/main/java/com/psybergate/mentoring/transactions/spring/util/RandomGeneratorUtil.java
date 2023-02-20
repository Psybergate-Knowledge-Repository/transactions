package com.psybergate.mentoring.transactions.spring.util;

import com.github.javafaker.Faker;
import com.psybergate.mentoring.transactions.spring.dto.Account;
import com.psybergate.mentoring.transactions.spring.dto.Customer;

import java.math.BigDecimal;

public class RandomGeneratorUtil {

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

    public static Account generateRandomUnassignedAccount() {
        final String name = FAKER.funnyName().name();
        final BigDecimal balance = BigDecimal.valueOf(Math.abs(FAKER.random().nextDouble()));
        return new Account(getRandomAccountNumber(), name, balance, 0L);
    }

    private static long getRandomAccountNumber() {
        return Double.valueOf(Math.random()*10E10).longValue();
    }
}

