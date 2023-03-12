package com.psybergate.mentoring.transactions.enums;

import lombok.Getter;

@Getter
public enum TransactionColour {
    RED("\u001B[31m"),
    GREEN("\u001B[32m");

    private final String code;

    TransactionColour(final String code) {
        this.code = code;
    }

    public static TransactionColour getTransactionColour(final int tranIdentifier) {
        return tranIdentifier == 1 ? RED : GREEN;
    }
}
