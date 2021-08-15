package com.jss.camel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TransactionDto implements Serializable {

    private String transactionId;
    private String senderAccountId;
    private String receiverAccountId;
    private String amount;
    private String currency;
    private String transactionDate;
}